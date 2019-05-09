package com.amplify.api.domain.venue

import com.amplify.api.domain.coin.CoinService
import com.amplify.api.domain.models.{PlaylistIdentifier, TrackIdentifier}
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.auth.VenueActionBuilder
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
    venueAuthAction: VenueActionBuilder)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def createCoins() = venueAuthAction.async(parse.json[CreateCoinsRequest]) { request ⇒
    coinService
        .createCoins(request.venue.uid, request.body.number)
        .map(_.map(coinToCreateCoinResponse))
  }

  def retrievePlaylists() = venueAuthAction.async(parse.empty) { request ⇒
    val eventualPlaylists = playlistService.retrievePlaylists(request.venue.uid)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }

  def retrievePlaylist(identifier: String) = venueAuthAction.async(parse.empty) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.venue.uid
      val eventualPlaylist = playlistService.retrievePlaylist(venueUid, playlistIdentifier)
      eventualPlaylist.map(playlistToPlaylistResponse)
    }
  }

  def setAllowedPlaylist() = venueAuthAction.async(parse.json[PlaylistRequest]) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.venue.uid
      queueService.setAllowedPlaylist(venueUid, playlistIdentifier).map(_ ⇒ NoContent)
    }
  }

  def retrieveAllowedPlaylist() = venueAuthAction.async(parse.empty) { request ⇒
    queueService.retrieveAllowedPlaylist(request.venue.uid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }

  def addPlaylistTracks() = venueAuthAction.async(parse.json[PlaylistRequest]) { request ⇒
    val triedIdentifier = PlaylistIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { playlistIdentifier ⇒
      val venueUid = request.venue.uid
      queueService.addPlaylistTracks(venueUid, playlistIdentifier).map(_ ⇒ NoContent)
    }
  }

  def addVenueTrack() = venueAuthAction.async(parse.json[AddTrackRequest]) { request ⇒
    val triedIdentifier = TrackIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { trackIdentifier ⇒
      val venueUid = request.venue.uid
      queueService.addVenueTrack(venueUid, trackIdentifier).map(_ ⇒ NoContent)
    }
  }

  def retrieveQueue() = venueAuthAction.async(parse.empty) { request ⇒
    queueService.retrieveQueue(request.venue.uid).map(queueToQueueResponse)
  }

  def start() = venueAuthAction.async(parse.empty) { request ⇒
    queueService.start(request.venue.uid).map(_ ⇒ NoContent)
  }

  def skip() = venueAuthAction.async(parse.empty) { request ⇒
    queueService.skip(request.venue.uid).map(_ ⇒ NoContent)
  }

  def finish() = venueAuthAction.async(parse.empty) { request ⇒
    queueService.finish(request.venue.uid).map(_ ⇒ NoContent)
  }
}
