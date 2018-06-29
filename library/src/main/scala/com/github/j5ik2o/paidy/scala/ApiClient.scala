package com.github.j5ik2o.paidy.scala

import com.github.j5ik2o.paidy.scala.model._
import monix.eval.Task

trait ApiClient {

  def capturePayment(paymentId: PaymentId, metadata: Map[String, String] = Map.empty): Task[Payment]

  def refundPayment(paymentId: PaymentId,
                    captureId: CaptureId,
                    amount: Option[BigDecimal] = None,
                    reason: Option[String] = None,
                    metadata: Map[String, String] = Map.empty): Task[Payment]

  def getPaymentById(paymentId: PaymentId): Task[Payment]

  def updatePayment(paymentId: PaymentId,
                    orderRef: Option[String] = None,
                    description: Option[String] = None,
                    metadata: Map[String, String] = Map.empty): Task[Payment]

  def closePayment(paymentId: PaymentId): Task[Payment]

  def getTokens: Task[Seq[Token]]

  def getToken(tokenId: TokenId): Task[Token]

  def suspendToken(tokenId: TokenId,
                   walletId: Option[WalletId] = None,
                   reason: Option[(TokenReasonType, String)] = None): Task[Token]

  def resumeToken(tokenId: TokenId,
                  walletId: Option[WalletId] = None,
                  reason: Option[(TokenReasonType, String)] = None): Task[Token]

  def deleteToken(tokenId: TokenId,
                  walletId: Option[WalletId] = None,
                  reason: Option[(TokenReasonType, String)] = None): Task[Token]
}
