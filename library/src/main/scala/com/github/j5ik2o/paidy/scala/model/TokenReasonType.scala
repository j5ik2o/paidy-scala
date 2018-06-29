package com.github.j5ik2o.paidy.scala.model

import enumeratum._

import scala.collection.immutable

sealed abstract class TokenReasonType(override val entryName: String) extends EnumEntry

object TokenReasonType extends Enum[TokenReasonType] {
  override def values: immutable.IndexedSeq[TokenReasonType] = findValues

  case object ConsumerRequested extends TokenReasonType("consumer.requested")
  case object MerchantRequested extends TokenReasonType("merchant.requested")
  case object FraudSuspected    extends TokenReasonType("fraud.suspected")
  case object General           extends TokenReasonType("general")
}
