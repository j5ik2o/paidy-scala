package com.github.j5ik2o.paidy.scala.model

import java.time.ZonedDateTime

import io.circe.Decoder

case class TokenId(value: String)

object TokenId {

  implicit val decoder: Decoder[TokenId] = Decoder[String].map(TokenId(_))

}

case class MerchantId(value: String)

object MerchantId {

  implicit val decoder: Decoder[MerchantId] = Decoder[String].map(MerchantId(_))

}

case class WalletId(value: String)

object WalletId {

  implicit val decoder: Decoder[WalletId] = Decoder[String].map(WalletId(_))

}

case class Origin(name1: String, name2: String, email: String, phone: String, address: String)

object Origin {

  implicit val decoder: Decoder[Origin] =
    Decoder.forProduct5("name1", "name2", "email", "phone", "address")(Origin.apply)

}

case class CustomerId(value: String)

object CustomerId {

  implicit val decoder: Decoder[CustomerId] = Decoder[String].map(CustomerId(_))

}

case class Suspension(timestamp: ZonedDateTime, authority: String)

object Suspension extends JsonImplicits {

  implicit val decoder: Decoder[Suspension] = Decoder.forProduct2("timestamp", "authority")(Suspension.apply)

}

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
                 deletedAt: Option[ZonedDateTime])

object Token extends JsonImplicits {

  implicit val decoder: Decoder[Token] = Decoder.forProduct17(
    "id",
    "test",
    "merchant_id",
    "wallet_id",
    "status",
    "origin",
    "description",
    "kind",
    "metadata",
    "webhook_url",
    "customer_id",
    "suspensions",
    "version_nr",
    "created_at",
    "updated_at",
    "activated_at",
    "deleted_at"
  )(Token.apply)

}
