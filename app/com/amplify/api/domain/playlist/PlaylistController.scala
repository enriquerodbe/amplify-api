package com.amplify.api.domain.playlist

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinAuthRequests
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier}
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class PlaylistController @Inject()(
    cc: ControllerComponents,
    playlistLogic: PlaylistLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with VenueAuthRequests with CoinAuthRequests {

  def retrievePlaylists() = authenticatedVenue(parse.empty) { request ⇒
    val eventualPlaylists = playlistLogic.retrievePlaylists(request.subject.venue)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }

  def retrieveCurrentPlaylist() = authenticatedVenue(parse.empty) { request ⇒
    doRetrieveCurrentPlaylist(request.subject.venue.uid)
  }

  def retrieveVenueCurrentPlaylist(uid: String) = authenticatedCoin() { _ ⇒
    doRetrieveCurrentPlaylist(Uid(uid))
  }

  private def doRetrieveCurrentPlaylist(venueUid: Uid): Future[Result] = {
    playlistLogic.retrieveCurrentPlaylist(venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: PlaylistIdentifier) ⇒
        playlistLogic.setCurrentPlaylist(request.subject.venue, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
