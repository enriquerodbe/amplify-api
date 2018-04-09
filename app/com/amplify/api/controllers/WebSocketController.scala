package com.amplify.api.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.amplify.api.aggregates.queue.EventNotifier
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.logic.VenueAuthLogic
import com.amplify.api.domain.models.Venue
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class WebSocketController @Inject()(
    cc:ControllerComponents,
    authHeadersUtil: AuthHeadersUtil,
    venueAuthLogic: VenueAuthLogic)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    materializer: Materializer) extends AbstractController(cc) {

  def connect: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    authHeadersUtil.getAuthTokenFromQueryString(request) match {
      case Success(authToken) ⇒
        venueAuthLogic.login(authToken).map {
          case Some(venue) ⇒ Right(notifierFlow(venue))
          case _ ⇒ Left(Forbidden)
        }
      case _ ⇒
        Future.successful(Left(Forbidden))
    }
  }

  private def notifierFlow(venue: Venue) = {
    ActorFlow.actorRef(EventNotifier.props(venue.uid, _))
  }
}
