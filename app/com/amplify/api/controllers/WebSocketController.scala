package com.amplify.api.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.amplify.api.aggregates.queue.EventNotifier
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.logic.VenueAuthLogic
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import scala.concurrent.{ExecutionContext, Future}

class WebSocketController @Inject()(
    cc:ControllerComponents,
    venueAuthLogic: VenueAuthLogic)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    materializer: Materializer) extends AbstractController(cc) {

  def connect: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    request.session.get(AuthHeadersUtil.VENUE_UID) match {
      case Some(venueUid) ⇒
        venueAuthLogic.login(Uid(venueUid)).map {
          case Some(venue) ⇒ Right(notifierFlow(venue))
          case _ ⇒ Left(Forbidden)
        }
      case _ ⇒
        Future.successful(Left(Forbidden))
    }
  }

  private def notifierFlow(venue: Venue) = ActorFlow.actorRef(EventNotifier.props(venue.uid, _))
}
