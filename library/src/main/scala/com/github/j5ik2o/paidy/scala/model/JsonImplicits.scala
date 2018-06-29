package com.github.j5ik2o.paidy.scala.model

import java.time._
import java.time.format.DateTimeFormatter

import io.circe.{Decoder, Encoder}

trait JsonImplicits {

  private val ft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  implicit val ZonedDateTimeEncoder: Encoder[ZonedDateTime] =
    Encoder[String].contramap { v =>
      v.format(ft)
    }

  implicit val ZonedDateTimeDecoder: Decoder[ZonedDateTime] =
    Decoder[String].map { v =>
      ZonedDateTime.parse(v, ft)
    }

}
