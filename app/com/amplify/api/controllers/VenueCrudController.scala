package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.dtos.Playlist.{PlaylistRequest, playlistToPlaylistResponse}
import com.amplify.api.controllers.dtos.Queue.queueToQueueResponse
import com.amplify.api.controllers.dtos.Track.trackToTrackResponse
import com.amplify.api.controllers.dtos.Venue.venueToVenueResponse
import com.amplify.api.domain.logic.{VenueCrudLogic, VenuePlayerLogic}
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.{ExecutionContext, Future}
import scala.language.reflectiveCalls
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueCrudController @Inject()(
    venueCrudLogic: VenueCrudLogic,
    venuePlayerLogic: VenuePlayerLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def fetchPlaylists(offset: Int, limit: Int) = authenticatedVenue() { request ⇒
    val eventualPlaylists = venueCrudLogic.retrievePlaylists(request.subject.venueReq)
    eventualPlaylists.map(playlists ⇒ Ok(Json.toJson(playlists.map(playlistToPlaylistResponse))))
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentProviderIdentifier.fromString(request.body.identifier) match {
      case Success(identifier) ⇒
        venueCrudLogic.setCurrentPlaylist(request.subject.venueReq, identifier).map(_ ⇒ NoContent)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def getQueue() = authenticatedVenue() { request ⇒
    venueCrudLogic.retrieveQueue(request.subject.venue.unauthenticated).map { queue ⇒
      Ok(Json.toJson(queueToQueueResponse(queue)))
    }
  }

  def fetchVenue() = authenticatedVenue() { request ⇒
    val response = venueToVenueResponse(request.subject.venue)
    Future.successful(Ok(Json.toJson(response)))
  }

  def retrieveAll() = authenticatedUser() { _ ⇒
    venueCrudLogic.retrieveAll().map { venues ⇒
      Ok(Json.toJson(venues.map(venueToVenueResponse)))
    }
  }

  def retrieveCurrentPlaylistTracks(uid: String) = authenticatedUser() { request ⇒
    venueCrudLogic.retrieveCurrentPlaylistTracks(uid, request.subject.userReq).map { tracks =>
      Ok(Json.toJson(tracks.map(trackToTrackResponse)))
    }
  }
}
