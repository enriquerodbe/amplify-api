package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.controllers.dtos.ClientErrorResponse
import com.amplify.api.domain.logic.{UserAuthLogic, VenueAuthLogic}
import com.amplify.api.domain.models.{AuthToken, UserReq, VenueReq}
import com.amplify.api.exceptions.AppExceptionCode
import play.api.mvc.{Request, Result, Results}
import play.mvc.Http
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

abstract class AbstractDeadboltHandler(
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends DeadboltHandler with Results {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    Future.successful(None)
  }

  override def getDynamicResourceHandler[A](
      request: Request[A]): Future[Option[DynamicResourceHandler]] = Future.successful(None)

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful {
      ClientErrorResponse(
        AppExceptionCode.AuthenticationFailed,
        "Authentication failed",
        Http.Status.FORBIDDEN)
    }
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    authHeadersUtil.getAuthTokenFromHeaders(request) match {
      case Success(authToken) ⇒ login(authToken)
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }

  protected def login(authToken: AuthToken): Future[Option[Subject]]
}

class UserDeadboltHandler(
    userAuthLogic: UserAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends AbstractDeadboltHandler(authHeadersUtil) {

  override protected def login(authToken: AuthToken): Future[Option[Subject]] = {
    userAuthLogic.login(authToken).map(_.map(user ⇒ AmplifyApiUser(UserReq(user, authToken))))
  }
}

class VenueDeadboltHandler(
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends AbstractDeadboltHandler(authHeadersUtil) {

  override protected def login(authToken: AuthToken): Future[Option[Subject]] = {
    venueAuthLogic.login(authToken).map(_.map(venue ⇒ AmplifyApiVenue(VenueReq(venue, authToken))))
  }
}

class EmptyHandler(authHeadersUtil: AuthHeadersUtil)(implicit ec: ExecutionContext)
  extends AbstractDeadboltHandler(authHeadersUtil) {

  override protected def login(authToken: AuthToken): Future[Option[Subject]] = {
    Future.successful(None)
  }
}
