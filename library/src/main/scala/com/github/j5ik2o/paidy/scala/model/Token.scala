package com.github.j5ik2o.paidy.scala.model

import java.time.ZonedDateTime

case class TokenId(value: String)
case class MerchantId(value: String)
case class WalletId(value: String)
case class Origin(name1: String, name2: String, email: String, phone: String, address: String)
case class CustomerId(value: String)
case class Suspension(timestamp: ZonedDateTime, authority: String)

case class Token(id: TokenId,
                 test: Boolean,
                 merchantId: MerchantId,
                 walletId: WalletId,
                 status: String,
                 origin: Origin,
                 description: String,
                 kind: String,
                 metadata: Map[String, String],
                 webhookUrl: String,
                 customerId: CustomerId,
                 suspensions: Seq[Suspension],
                 versionNr: BigDecimal,
                 createdAt: ZonedDateTime,
                 updatedAt: Option[ZonedDateTime],
                 activatedAt: Option[ZonedDateTime],
                 deletedAt: Option[ZonedDateTime]) {}
