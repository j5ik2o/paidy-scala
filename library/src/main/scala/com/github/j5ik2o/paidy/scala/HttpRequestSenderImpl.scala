package com.github.j5ik2o.paidy.scala

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, RawHeader}
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source, SourceQueueWithComplete, Unzip, Zip}
import io.circe.{Decoder, Json}
import io.circe.parser.parse
import monix.eval.Task
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

case class ApiConfig(host: String, port: Int, timeoutForToStrict: FiniteDuration, requestBufferSize: Int)

class HttpRequestSenderImpl(config: ApiConfig)(implicit system: ActorSystem) extends HttpRequestSender {

  implicit val materializer = ActorMaterializer()

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val poolClientFlow =
    Http().cachedHostConnectionPoolHttps[Int](config.host, config.port)

  private val timeout: FiniteDuration = config.timeoutForToStrict

  private def toJson(jsonString: String): Task[Json] = Task.deferFuture {
    parse(jsonString) match {
      case Right(json) =>
        logger.debug("json = {}", json.spaces2)
        Future.successful(json)
      case Left(error) => Future.failed(JsonParsingException(error.message))
    }
  }

  private def toModel[A](json: Json)(implicit d: Decoder[A]): Task[A] = Task.deferFuture {
    json.as[A] match {
      case Right(r) => Future.successful(r)
      case Left(error) =>
        Future.failed(JsonDecodingException(error.message + ":" + error.history))
    }
  }

  case class ErrorResponse(reference: String, status: String, code: String, title: String, description: String)

  private def responseToModel[A](responseFuture: Task[HttpResponse])(implicit d: Decoder[A]): Task[A] = {
    for {
      httpResponse <- responseFuture
      httpEntity   <- Task.deferFuture(httpResponse.entity.toStrict(timeout))
      json         <- toJson(httpEntity.data.utf8String)
      model <- if (httpResponse.status == StatusCodes.OK) toModel(json)
      else {
        Task.deferFuture {
          import io.circe.generic.auto._
          json.as[ErrorResponse] match {
            case Right(
                ErrorResponse(reference: String, status: String, code: String, title: String, description: String)
                ) =>
              Future.failed(PaidyApiException(reference, status, code, title, description))
            case Left(error) =>
              Future.failed(JsonDecodingException(error.message + ":" + error.history))
          }
        }
      }
    } yield model
  }

  private case class PromiseWithHttpRequest(promise: Promise[HttpResponse], request: HttpRequest)

  private def sendRequestFlow
    : Flow[(HttpRequest, PromiseWithHttpRequest), (Try[HttpResponse], PromiseWithHttpRequest), NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val unzip = b.add(Unzip[HttpRequest, PromiseWithHttpRequest])
      val zip   = b.add(Zip[Try[HttpResponse], PromiseWithHttpRequest])
      unzip.out0 ~> Flow[HttpRequest]
        .map((_, 1)) ~> poolClientFlow
        .map(_._1)
        .log("request")
        .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel)) ~> zip.in0
      unzip.out1 ~> zip.in1
      FlowShape(unzip.in, zip.out)
    })

  private val requestQueue: SourceQueueWithComplete[PromiseWithHttpRequest] = Source
    .queue[PromiseWithHttpRequest](config.requestBufferSize, OverflowStrategy.dropNew)
    .map {
      case p @ PromiseWithHttpRequest(_, request) => (request, p)
    }
    .via(sendRequestFlow)
    .map {
      case (triedResponse, PromiseWithHttpRequest(promise, _)) =>
        promise.complete(triedResponse)
    }
    .toMat(Sink.ignore)(Keep.left)
    .run()

  override def shutdown(): Unit = {
    requestQueue.complete()
    Await.result(requestQueue.watchCompletion(), Duration.Inf)
  }

  override def sendRequest[A: Decoder](request: HttpRequest,
                                       secretKey: String,
                                       headers: List[RawHeader] = List.empty): Task[A] = {
    val responseTask = Task.deferFutureAction { implicit ec =>
      val promise = Promise[HttpResponse]()
      logger.debug("request = {}", request.toString())
      requestQueue
        .offer(
          PromiseWithHttpRequest(promise,
                                 request.withHeaders(headers).addCredentials(BasicHttpCredentials(secretKey, "")))
        )
        .flatMap {
          case QueueOfferResult.Enqueued =>
            promise.future
          case QueueOfferResult.Failure(t) =>
            Future.failed(HttpRequestSendException("Failed to send request", Some(t)))
          case QueueOfferResult.Dropped =>
            Future.failed(
              HttpRequestSendException(
                s"Failed to send request, the queue buffer was full."
              )
            )
          case QueueOfferResult.QueueClosed =>
            Future.failed(HttpRequestSendException("Failed to send request, the queue was closed"))
        }
    }
    responseToModel[A](responseTask)
  }
}
