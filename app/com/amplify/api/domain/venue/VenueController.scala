package com.amplify.api.domain.venue

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinService
import com.amplify.api.domain.models.{PlaylistIdentifier, TrackIdentifier}
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.CoinDtos.{CreateCoinsRequest, coinToCreateCoinResponse}
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.shared.controllers.dtos.QueueDtos.{AddTrackRequest, queueToQueueResponse}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off public.methods.have.type
class VenueController @Inject()(
    cc: ControllerComponents,
    coinService: CoinService,
    playlistService: PlaylistService,
    queueService: QueueService,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with VenueAuthRequests {

  def createCoins() = authenticatedVenue(parse.json[CreateCoinsRequest]) { request ⇒
    coinService
        .createCoins(request.subject.venue.uid, request.body.number)
        .map(_.map(coinToCreateCoinResponse))
  }

  def retrievePlaylists() = authenticatedVenue(parse.empty) { request ⇒
    val eventualPlaylists = playlistService.retrievePlaylists(request.subject.venue.uid)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }

  def retrievePlaylist(identifier: String) = authenticatedVenue(parse.empty) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.subject.venue.uid
      val eventualPlaylist = playlistService.retrievePlaylist(venueUid, playlistIdentifier)
      eventualPlaylist.map(playlistToPlaylistResponse)
    }
  }

  def setAllowedPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.subject.venue.uid
      queueService.setAllowedPlaylist(venueUid, playlistIdentifier).map(_ ⇒ NoContent)
    }
  }

  def retrieveAllowedPlaylist() = authenticatedVenue(parse.empty) { request ⇒
    queueService.retrieveAllowedPlaylist(request.subject.venue.uid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }

  def addPlaylistTracks() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.subject.venue.uid
      queueService.addPlaylistTracks(venueUid, playlistIdentifier).map(_ ⇒ NoContent)
    }
  }

  def addVenueTrack() = authenticatedVenue(parse.json[AddTrackRequest]) { request ⇒
    val triedIdentifier = TrackIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { trackIdentifier ⇒
      val venueUid = request.subject.venue.uid
      queueService.addVenueTrack(venueUid, trackIdentifier).map(_ ⇒ NoContent)
    }
  }

  def retrieveQueue() = authenticatedVenue(parse.empty) { request ⇒
    queueService.retrieveQueue(request.subject.venue.uid).map(queueToQueueResponse)
  }

  def start() = authenticatedVenue(parse.empty) { request ⇒
    queueService.start(request.subject.venue.uid).map(_ ⇒ NoContent)
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    queueService.skip(request.subject.venue.uid).map(_ ⇒ NoContent)
  }

  def finish() = authenticatedVenue(parse.empty) { request ⇒
    queueService.finish(request.subject.venue.uid).map(_ ⇒ NoContent)
  }
}
