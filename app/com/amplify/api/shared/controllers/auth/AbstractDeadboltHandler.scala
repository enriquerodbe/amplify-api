package com.amplify.api.shared.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.shared.controllers.dtos.ClientErrorResponse
import com.amplify.api.shared.exceptions.AppExceptionCode.AuthenticationFailed
import play.api.mvc.{Request, Result, Results}
import play.mvc.Http.Status.FORBIDDEN
import scala.concurrent.Future

abstract class AbstractDeadboltHandler extends DeadboltHandler with Results {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    Future.successful(None)
  }

  override def getDynamicResourceHandler[A](
      request: Request[A]): Future[Option[DynamicResourceHandler]] = Future.successful(None)

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(ClientErrorResponse(AuthenticationFailed, "Authentication failed", FORBIDDEN))
  }
}

object EmptyHandler extends AbstractDeadboltHandler {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    Future.successful(None)
  }
}
