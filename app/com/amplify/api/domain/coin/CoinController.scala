package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.models.{ContentIdentifier, TrackIdentifier}
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.shared.controllers.dtos.CoinDtos.coinStatusToCoinStatusResponse
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.playlistToPlaylistResponse
import com.amplify.api.shared.controllers.dtos.QueueDtos.AddTrackRequest
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class CoinController @Inject()(
    cc: ControllerComponents,
    coinService: CoinService,
    queueService: QueueService,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with CoinAuthRequests {

  def coinStatus() = authenticatedCoin(parse.empty) { request ⇒
    coinService
      .retrieveStatus(request.subject.coin)
      .map(coinStatusToCoinStatusResponse)
  }

  def addTrack() = authenticatedCoin(parse.json[AddTrackRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: TrackIdentifier) ⇒
        val coinCode = request.subject.coin
        queueService
            .addTrack(coinCode.venueUid, coinCode.code, identifier)
            .map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }

  def retrieveCurrentPlaylist() = authenticatedCoin() { request ⇒
    queueService.retrieveAllowedPlaylist(request.subject.coin.venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case _ ⇒ NoContent
    }
  }
}
