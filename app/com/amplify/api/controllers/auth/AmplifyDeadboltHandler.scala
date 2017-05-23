package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.amplify.api.domain.logic.UserAuthLogic
import com.amplify.api.domain.models.{AuthToken, AuthenticatedUserReq, AuthenticatedVenue, AuthenticatedVenueReq}
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
      case Success(authToken) ⇒ login(authToken)
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }

  private def login(authToken: AuthToken) = userAuthLogic.login(authToken).map {
    case (user, Some(venue)) ⇒
      val authVenue = AuthenticatedVenue(venue.id, user, venue.name)
      Some(AmplifyApiVenue(AuthenticatedVenueReq(authVenue, authToken)))
    case (user, _) ⇒
      Some(AmplifyApiUser(AuthenticatedUserReq(user, authToken)))
  }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(Forbidden)
  }
}
