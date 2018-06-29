package com.github.j5ik2o.paidy.scala

case class PaidyApiException(reference: String, status: String, code: String, title: String, description: String)
    extends Exception(s"occurred error response: $reference, $status, $code, $title, $description")
