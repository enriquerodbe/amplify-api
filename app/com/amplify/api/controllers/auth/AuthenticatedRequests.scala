package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.HandlerKeys.{CoinHandlerKey, VenueHandlerKey}
import play.api.mvc._
import scala.concurrent.Future
import scala.language.reflectiveCalls

trait AuthenticatedRequests { self: AbstractController ⇒

  def actionBuilder: ActionBuilders

  case class AuthenticatedCoinRequest[A](
      subject: CoinSubject,
      request: Request[A]) extends WrappedRequest[A](request)

  case class AuthenticatedVenueRequest[A](
      subject: VenueSubject,
      request: Request[A]) extends WrappedRequest[A](request)

  def authenticatedCoin[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedCoinRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(CoinHandlerKey).apply(parser) { request ⇒
      request.subject match {
        case Some(coin: CoinSubject) ⇒
          block(AuthenticatedCoinRequest[A](coin, request))
        case other ⇒
          throw new IllegalStateException(s"Expected CoinSubject, got: $other")
      }
    }
  }

  def authenticatedVenue[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedVenueRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(VenueHandlerKey).apply(parser) { request ⇒
      request.subject match {
        case Some(venue: VenueSubject) ⇒
          block(AuthenticatedVenueRequest[A](venue, request))
        case other ⇒
          throw new IllegalStateException(s"Expected VenueSubject, got: $other")
      }
    }
  }
}
