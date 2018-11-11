package com.amplify.api.domain.venue.auth

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.venue.VenueSubject
import com.amplify.api.shared.controllers.auth.HandlerKeys.VenueHandlerKey
import play.api.mvc._
import scala.concurrent.Future
import scala.language.reflectiveCalls

trait VenueAuthRequests { self: AbstractController ⇒

  def actionBuilder: ActionBuilders

  case class AuthenticatedVenueRequest[A](
      subject: VenueSubject,
      request: Request[A]) extends WrappedRequest[A](request)

  def authenticatedVenue[A](
      parser: BodyParser[A] = parse.anyContent)(
      block: AuthenticatedVenueRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().key(VenueHandlerKey)(parser) { request ⇒
      request.subject match {
        case Some(venue: VenueSubject) ⇒
          block(AuthenticatedVenueRequest[A](venue, request))
        case other ⇒
          throw new IllegalStateException(s"Expected VenueSubject, got: $other")
      }
    }
  }
}
