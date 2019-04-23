package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.shared.controllers.auth.HandlerKeys.CoinHandlerKey
import play.api.mvc._
import scala.concurrent.Future
import scala.language.reflectiveCalls

private[coin] trait CoinAuthRequests { self: AbstractController ⇒

  def actionBuilder: ActionBuilders

  case class AuthenticatedCoinRequest[A](
      subject: CoinSubject,
      request: Request[A]) extends WrappedRequest[A](request)

  def authenticatedCoin[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedCoinRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(CoinHandlerKey)(parser) { request ⇒
      request.subject match {
        case Some(coin: CoinSubject) ⇒
          block(AuthenticatedCoinRequest[A](coin, request))
        case other ⇒
          throw new IllegalStateException(s"Expected CoinSubject, got: $other")
      }
    }
  }
}
