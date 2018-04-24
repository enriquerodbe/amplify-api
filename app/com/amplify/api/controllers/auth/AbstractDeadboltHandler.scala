package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.controllers.dtos.ClientErrorResponse
import com.amplify.api.domain.logic.{CoinLogic, VenueAuthLogic}
import com.amplify.api.domain.models.VenueReq
import com.amplify.api.exceptions.AppExceptionCode.AuthenticationFailed
import play.api.mvc.{Request, Result, Results}
import play.mvc.Http.Status.FORBIDDEN
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
    Future.successful(ClientErrorResponse(AuthenticationFailed, "Authentication failed", FORBIDDEN))
  }
}

class CoinDeadboltHandler(
    coinLogic: CoinLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends AbstractDeadboltHandler(authHeadersUtil) {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    authHeadersUtil.getCoinFromHeaders(request) match {
      case Success(coin) ⇒ coinLogic.login(coin).map(_.map(CoinSubject(_)))
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }
}

class VenueDeadboltHandler(
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends AbstractDeadboltHandler(authHeadersUtil) {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    authHeadersUtil.getAuthTokenFromHeaders(request) match {
      case Success(authToken) ⇒
        val eventualMaybeVenue = venueAuthLogic.login(authToken)
        eventualMaybeVenue.map(_.map(venue ⇒ VenueSubject(VenueReq(venue, authToken))))

      case Failure(ex) ⇒ Future.failed(ex)
    }
  }
}

class EmptyHandler(authHeadersUtil: AuthHeadersUtil)(implicit ec: ExecutionContext)
  extends AbstractDeadboltHandler(authHeadersUtil) {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    Future.successful(None)
  }
}
