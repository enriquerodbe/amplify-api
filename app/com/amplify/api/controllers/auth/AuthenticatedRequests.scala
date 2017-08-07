package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.models.{AuthToken, AuthenticatedUserReq}
import com.amplify.api.exceptions.VenueNotFoundByUserIdentifier
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
    actionBuilder.SubjectPresentAction().defaultHandler.apply(parser) { request ⇒
      request.subject match {
        case Some(authUser: AmplifyApiUser) ⇒
          block(AuthenticatedUserRequest[A](authUser, request))
        case Some(authVenue: AmplifyApiVenue) ⇒
          val userReq = AuthenticatedUserReq(authVenue.user, authVenue.venueReq.authToken)
          block(AuthenticatedUserRequest[A](AmplifyApiUser(userReq), request))
        case other ⇒
          throw new IllegalStateException(s"Expected AuthUser, got: $other")
      }
    }
  }

  def authenticatedVenue[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedVenueRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().defaultHandler.apply(parser) { request ⇒
      request.subject match {
        case Some(authUser: AmplifyApiUser) ⇒
          throw VenueNotFoundByUserIdentifier(authUser.userReq.identifier)
        case Some(authVenue: AmplifyApiVenue) ⇒
          block(AuthenticatedVenueRequest[A](authVenue, request))
        case other ⇒
          throw new IllegalStateException(s"Expected AuthVenue, got: $other")
      }
    }
  }
}
