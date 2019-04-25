package com.amplify.api.domain.venue

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinService
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier}
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.CoinDtos.{CreateCoinsRequest, coinToCoinResponse}
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.{PlaylistRequest, playlistInfoToPlaylistInfoResponse, playlistToPlaylistResponse}
import com.amplify.api.shared.controllers.dtos.QueueDtos.queueToQueueResponse
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

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
        .map(_.map(coinToCoinResponse))
  }

  def retrievePlaylists() = authenticatedVenue(parse.empty) { request ⇒
    val eventualPlaylists = playlistService.retrievePlaylists(request.subject.venue.uid)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }

  def retrievePlaylist(identifier: String) = authenticatedVenue(parse.empty) { request ⇒
    withPlaylistIdentifier(identifier) { playlistIdentifier ⇒
      val venueUid = request.subject.venue.uid
      val eventualPlaylist = playlistService.retrievePlaylist(venueUid, playlistIdentifier)
      eventualPlaylist.map(playlistToPlaylistResponse)
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    withPlaylistIdentifier(request.body.identifier) { playlistIdentifier ⇒
      val venueUid = request.subject.venue.uid
      queueService.setCurrentPlaylist(venueUid, playlistIdentifier).map(_ ⇒ NoContent)
    }
  }

  private def withPlaylistIdentifier[T](
      identifier: String)(
      onSuccess: PlaylistIdentifier ⇒ Future[T]): Future[T] = {
    ContentIdentifier.fromString(identifier) match {
      case Success(playlistIdentifier: PlaylistIdentifier) ⇒
        onSuccess(playlistIdentifier)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def retrieveCurrentPlaylist() = authenticatedVenue(parse.empty) { request ⇒
    queueService.retrieveCurrentPlaylist(request.subject.venue.uid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
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
