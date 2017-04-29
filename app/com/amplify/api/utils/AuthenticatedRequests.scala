package com.amplify.api.utils

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AmplifyApiSubject
import play.api.mvc._
import scala.concurrent.Future
import scala.language.reflectiveCalls

trait AuthenticatedRequests {

  def actionBuilder: ActionBuilders

  class AuthenticatedRequest[A](
      val subject: AmplifyApiSubject,
      request: Request[A]) extends WrappedRequest[A](request)

  def authenticated[A](
      parser: BodyParser[A] = BodyParsers.parse.anyContent)(
      block: AuthenticatedRequest[A] ⇒ Future[Result]): Action[A] = {
    actionBuilder.SubjectPresentAction().defaultHandler.apply(parser) { request ⇒
      request.subject match {
        case Some(authUser: AmplifyApiSubject) ⇒
          block(new AuthenticatedRequest[A](authUser, request))
        case other ⇒
          throw new IllegalStateException(s"Expected AuthUser, got: $other")
      }
    }
  }
}
