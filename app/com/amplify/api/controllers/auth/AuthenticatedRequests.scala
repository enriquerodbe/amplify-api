package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.HandlerKeys.{UserHandlerKey, VenueHandlerKey}
import com.amplify.api.domain.models.AuthToken
import play.api.mvc._
import scala.concurrent.Future
import scala.language.reflectiveCalls

trait AuthenticatedRequests { self: AbstractController ⇒

  def actionBuilder: ActionBuilders

  case class AuthenticatedUserRequest[A](
      subject: AmplifyApiUser,
      request: Request[A]) extends WrappedRequest[A](request) {

    def authToken: AuthToken = subject.userReq.authToken
  }

  case class AuthenticatedVenueRequest[A](
      subject: AmplifyApiVenue,
      request: Request[A]) extends WrappedRequest[A](request) {

    def authToken: AuthToken = subject.venueReq.authToken
  }

  def authenticatedUser[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedUserRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(UserHandlerKey).apply(parser) { request ⇒
      request.subject match {
        case Some(user: AmplifyApiUser) ⇒
          block(AuthenticatedUserRequest[A](user, request))
        case other ⇒
          throw new IllegalStateException(s"Expected AuthUser, got: $other")
      }
    }
  }

  def authenticatedVenue[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedVenueRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(VenueHandlerKey).apply(parser) { request ⇒
      request.subject match {
        case Some(venue: AmplifyApiVenue) ⇒
          block(AuthenticatedVenueRequest[A](venue, request))
        case other ⇒
          throw new IllegalStateException(s"Expected AuthVenue, got: $other")
      }
    }
  }
}
