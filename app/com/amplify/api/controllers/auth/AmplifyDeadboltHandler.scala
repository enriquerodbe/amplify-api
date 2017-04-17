package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.domain.logic.UserAuthLogic
import com.amplify.api.exceptions.{UserAuthTokenNotFound, UserNotFound}
import play.api.mvc.{Request, Result, Results}
import scala.concurrent.{ExecutionContext, Future}

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
    val eventualSubject =
      for {
        authData ← authHeadersUtil.getAuthData(request)
        user ← userAuthLogic.login(authData.authProviderType, authData.authToken)
      }
      yield Some(AuthUser(user.email, Nil, Nil))

    eventualSubject recover {
      case _: UserAuthTokenNotFound | _: UserNotFound ⇒ None
    }
  }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(Unauthorized)
  }
}
