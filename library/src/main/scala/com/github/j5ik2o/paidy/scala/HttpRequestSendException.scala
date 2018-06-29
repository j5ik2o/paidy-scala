package com.github.j5ik2o.paidy.scala

case class HttpRequestSendException(message: String, cause: Option[Throwable] = None) extends Exception(message)
