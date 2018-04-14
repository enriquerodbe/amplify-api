package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.Playlist.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.controllers.dtos.Queue.queueToQueueResponse
import com.amplify.api.controllers.dtos.SuccessfulResponse
import com.amplify.api.controllers.dtos.Venue.venueToVenueResponse
import com.amplify.api.domain.logic.{VenueCrudLogic, VenuePlayerLogic}
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier}
import com.amplify.api.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
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
      SuccessfulResponse(playlists.map(playlistInfoToPlaylistInfoResponse))
    }
  }

  def retrieveCurrentPlaylist(uid: String) = authenticatedUser() { _ ⇒
    val venueUid = Uid(uid)
    venueCrudLogic.retrieveCurrentPlaylist(venueUid).map {
      case Some(playlist) ⇒ SuccessfulResponse(playlistToPlaylistResponse(playlist))
      case _ ⇒ NoContent
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: PlaylistIdentifier) ⇒
        venueCrudLogic.setCurrentPlaylist(request.subject.venueReq, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def retrieveQueue() = authenticatedVenue() { request ⇒
    venueCrudLogic.retrieveQueue(request.subject.venue).map { queue ⇒
      SuccessfulResponse(queueToQueueResponse(queue))
    }
  }

  def retrieveCurrent() = authenticatedVenue() { request ⇒
    val response = venueToVenueResponse(request.subject.venue)
    Future.successful(SuccessfulResponse(response))
  }
}
