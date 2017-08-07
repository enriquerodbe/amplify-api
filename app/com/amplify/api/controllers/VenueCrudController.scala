package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.FcmToken.FcmTokenRequest
import com.amplify.api.controllers.dtos.Playlist.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.controllers.dtos.Queue.queueToQueueResponse
import com.amplify.api.controllers.dtos.Venue.venueToVenueResponse
import com.amplify.api.domain.logic.{VenueCrudLogic, VenuePlayerLogic}
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.domain.models.primitives.Token
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueCrudController @Inject()(
    cc: ControllerComponents,
    venueCrudLogic: VenueCrudLogic,
    venuePlayerLogic: VenuePlayerLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def retrievePlaylists() = authenticatedVenue() { request ⇒
    val eventualPlaylists = venueCrudLogic.retrievePlaylists(request.subject.venueReq)
    eventualPlaylists.map { playlists =>
      Ok(Json.toJson(playlists.map(playlistInfoToPlaylistInfoResponse)))
    }
  }

  def retrieveCurrentPlaylist(uid: String) = authenticatedUser() { _ ⇒
    venueCrudLogic.retrieveCurrentPlaylist(uid).map {
      case Some(playlist) ⇒ Ok(Json.toJson(playlistToPlaylistResponse(playlist)))
      case _ ⇒ NoContent
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentProviderIdentifier.fromString(request.body.identifier) match {
      case Success(identifier) ⇒
        venueCrudLogic.setCurrentPlaylist(request.subject.venueReq, identifier).map(_ ⇒ NoContent)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def retrieveQueue() = authenticatedVenue() { request ⇒
    venueCrudLogic.retrieveQueue(request.subject.venueReq).map { queue ⇒
      Ok(Json.toJson(queueToQueueResponse(queue)))
    }
  }

  def retrieveCurrent() = authenticatedVenue() { request ⇒
    val response = venueToVenueResponse(request.subject.venue)
    Future.successful(Ok(Json.toJson(response)))
  }

  def retrieveAll() = authenticatedUser() { _ ⇒
    venueCrudLogic.retrieveAll().map { venues ⇒
      Ok(Json.toJson(venues.map(venueToVenueResponse)))
    }
  }

  def setFcmToken() = authenticatedVenue(parse.json[FcmTokenRequest]) { request ⇒
    venueCrudLogic.setFcmToken(request.subject.venue, Token(request.body.token)).map(_ ⇒ NoContent)
  }
}
