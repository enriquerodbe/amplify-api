package com.amplify.api.domain.queue

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinAuthRequests
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier, TrackIdentifier}
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.{PlaylistRequest, playlistToPlaylistResponse}
import com.amplify.api.shared.controllers.dtos.QueueDtos._
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class QueueController @Inject()(
    cc: ControllerComponents,
    queueService: QueueService,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with VenueAuthRequests with CoinAuthRequests {

  def retrieveQueue() = authenticatedVenue(parse.empty) { request ⇒
    queueService.retrieveQueue(request.subject.venue).map(queueToQueueResponse)
  }

  def start() = authenticatedVenue(parse.empty) { request ⇒
    queueService.start(request.subject.venue).map(_ ⇒ NoContent)
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    queueService.skip(request.subject.venue).map(_ ⇒ NoContent)
  }

  def finish() = authenticatedVenue(parse.empty) { request ⇒
    queueService.finish(request.subject.venue).map(_ ⇒ NoContent)
  }

  def addTrack(uid: String) = authenticatedCoin(parse.json[AddTrackRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: TrackIdentifier) ⇒
        queueService
          .addTrack(Uid(uid), request.subject.coin.code, identifier)
          .map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def setCurrentPlaylist() = authenticatedVenue(parse.json[PlaylistRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: PlaylistIdentifier) ⇒
        queueService.setCurrentPlaylist(request.subject.venue, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def retrieveCurrentPlaylist() = authenticatedVenue(parse.empty) { request ⇒
    doRetrieveCurrentPlaylist(request.subject.venue.uid)
  }

  def retrieveVenueCurrentPlaylist(uid: String) = authenticatedCoin() { _ ⇒
    doRetrieveCurrentPlaylist(Uid(uid))
  }

  private def doRetrieveCurrentPlaylist(venueUid: Uid): Future[Result] = {
    queueService.retrieveCurrentPlaylist(venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }
}
