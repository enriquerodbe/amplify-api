package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.domain.logic.UserAuthLogic
import com.amplify.api.domain.models.{AuthToken, AuthenticatedUserReq}
import play.api.mvc.{Request, Result, Results}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AmplifyDeadboltHandler(
    userAuthLogic: UserAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends DeadboltHandler with Results {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    Future.successful(None)
  }

  override def getDynamicResourceHandler[A](
      request: Request[A]): Future[Option[DynamicResourceHandler]] = Future.successful(None)

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    authHeadersUtil.getAuthToken(request) match {
      case Success(authToken) ⇒ loginUser(authToken)
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }

  private def loginUser(authToken: AuthToken) = {
    userAuthLogic.login(authToken).map { user ⇒
      Some(AmplifyApiSubject(AuthenticatedUserReq(user, authToken.token)))
    }
  }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(Forbidden)
  }
}
