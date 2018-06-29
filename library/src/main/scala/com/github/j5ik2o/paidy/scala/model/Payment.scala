package com.github.j5ik2o.paidy.scala.model

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

case class ShippingAddress(zip: String,
                           state: Option[String],
                           city: Option[String],
                           line1: Option[String],
                           line2: Option[String])

case class CaptureId(value: String)
case class Capture(id: CaptureId,
                   amount: BigDecimal,
                   tax: Option[BigDecimal],
                   shipping: Option[BigDecimal],
                   orderItems: Seq[OrderItem],
                   metadata: Map[String, String],
                   createdAt: String)

case class RefundId(value: String)
case class Refund(id: RefundId,
                  captureId: CaptureId,
                  amount: BigDecimal,
                  reason: String,
                  metadata: Map[String, String],
                  createdAt: String)

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
                   expiresAt: String,
                   createdAt: String,
                   updatedAt: Option[String])
