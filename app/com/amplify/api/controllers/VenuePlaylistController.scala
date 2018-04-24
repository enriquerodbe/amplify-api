package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.Playlist.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.domain.logic.VenuePlaylistLogic
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier}
import com.amplify.api.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenuePlaylistController @Inject()(
    cc: ControllerComponents,
    venuePlaylistLogic: VenuePlaylistLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def retrievePlaylists() = authenticatedVenue(parse.empty) { request ⇒
    val eventualPlaylists = venuePlaylistLogic.retrievePlaylists(request.subject.venueReq)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }

  def retrieveCurrentPlaylist() = authenticatedVenue(parse.empty) { request ⇒
    doRetrieveCurrentPlaylist(request.subject.venue.uid)
  }

  def retrieveVenueCurrentPlaylist(uid: String) = authenticatedCoin() { _ ⇒
    doRetrieveCurrentPlaylist(Uid(uid))
  }

  private def doRetrieveCurrentPlaylist(venueUid: Uid): Future[Result] = {
    venuePlaylistLogic.retrieveCurrentPlaylist(venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: PlaylistIdentifier) ⇒
        val venueReq = request.subject.venueReq
        venuePlaylistLogic.setCurrentPlaylist(venueReq, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
