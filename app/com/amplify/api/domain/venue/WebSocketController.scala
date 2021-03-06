package com.amplify.api.domain.venue

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.venue.auth.{VenueAuthService, VenueAuthenticatedBuilder}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import scala.concurrent.{ExecutionContext, Future}

class WebSocketController @Inject()(
    cc:ControllerComponents,
    venueAuthService: VenueAuthService)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    materializer: Materializer) extends AbstractController(cc) {

  def connect: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    request.session.get(VenueAuthenticatedBuilder.VENUE_UID) match {
      case Some(venueUid) ⇒
        venueAuthService.login(Uid(venueUid)).map {
          case Some(venue) ⇒ Right(notifierFlow(venue))
          case _ ⇒ Left(Forbidden)
        }
      case _ ⇒
        Future.successful(Left(Forbidden))
    }
  }

  private def notifierFlow(venue: Venue) = ActorFlow.actorRef(VenueNotifier.props(venue, _))
}
