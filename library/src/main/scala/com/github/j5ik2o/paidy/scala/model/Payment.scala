package com.github.j5ik2o.paidy.scala.model

import java.time.ZonedDateTime

import io.circe.Decoder

case class PaymentId(value: String)

object PaymentId {

  implicit val decoder: Decoder[PaymentId] = Decoder[String].map(PaymentId(_))

}

case class Buyer(name1: String, name2: Option[String], email: String, phone: Option[String])

object Buyer {

  implicit val decoder: Decoder[Buyer] = Decoder.forProduct4("name1", "name2", "email", "phone")(Buyer.apply)

}

case class OrderItemId(value: String)

object OrderItemId {

  implicit val decoder: Decoder[OrderItemId] = Decoder[String].map(OrderItemId(_))

}

case class OrderItem(id: OrderItemId,
                     title: String,
                     description: Option[String],
                     unitPrice: BigDecimal,
                     quantity: BigDecimal)

object OrderItem {

  implicit val decoder: Decoder[OrderItem] =
    Decoder.forProduct5("id", "title", "description", "unit_price", "quantity")(OrderItem.apply)

}

case class Order(tax: Option[BigDecimal],
                 shipping: Option[BigDecimal],
                 orderRef: Option[String],
                 orderItems: Seq[OrderItem])

object Order {

  implicit val decoder: Decoder[Order] = Decoder.forProduct4("tax", "shipping", "order_ref", "order_items")(Order.apply)

}

case class ShippingAddress(zip: String,
                           state: Option[String],
                           city: Option[String],
                           line1: Option[String],
                           line2: Option[String])

object ShippingAddress {

  implicit val decoder: Decoder[ShippingAddress] =
    Decoder.forProduct5("zip", "state", "city", "line1", "line2")(ShippingAddress.apply)

}

case class CaptureId(value: String)

object CaptureId {

  implicit val decoder: Decoder[CaptureId] = Decoder[String].map(CaptureId(_))
}

case class Capture(id: CaptureId,
                   amount: BigDecimal,
                   tax: Option[BigDecimal],
                   shipping: Option[BigDecimal],
                   orderItems: Seq[OrderItem],
                   metadata: Map[String, String],
                   createdAt: ZonedDateTime)

object Capture extends JsonImplicits {

  implicit val decoder: Decoder[Capture] =
    Decoder.forProduct7("id", "amount", "tax", "shipping", "order_items", "metadata", "created_at")(Capture.apply)

}

case class RefundId(value: String)

object RefundId {

  implicit val decoder: Decoder[RefundId] = Decoder[String].map(RefundId(_))

}

case class Refund(id: RefundId,
                  captureId: CaptureId,
                  amount: BigDecimal,
                  reason: String,
                  metadata: Map[String, String],
                  createdAt: ZonedDateTime)

object Refund extends JsonImplicits {

  implicit val decoder: Decoder[Refund] =
    Decoder.forProduct6("id", "capture_id", "amount", "reason", "metadata", "created_at")(Refund.apply)

}

case class Payment(id: PaymentId,
                   status: String,
                   test: Boolean,
                   description: Option[String],
                   storeName: String,
                   amount: BigDecimal,
                   currency: String,
                   buyer: Buyer,
                   order: Order,
                   shippingAddress: ShippingAddress,
                   captures: Seq[Capture],
                   refunds: Seq[Refund],
                   metadata: Map[String, String],
                   expiresAt: ZonedDateTime,
                   createdAt: ZonedDateTime,
                   updatedAt: Option[ZonedDateTime])

object Payment extends JsonImplicits {

  implicit val decoder: Decoder[Payment] = Decoder.forProduct16(
    "id",
    "status",
    "test",
    "description",
    "store_name",
    "amount",
    "currency",
    "buyer",
    "order",
    "shipping_address",
    "capture",
    "refunds",
    "metadata",
    "expires_at",
    "created_at",
    "updated_at"
  )(Payment.apply)

}
