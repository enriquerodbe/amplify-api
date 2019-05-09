package com.amplify.api.domain.coin

import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.shared.controllers.dtos.CoinDtos.CoinRemainingUsagesResponse
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.playlistToPlaylistResponse
import com.amplify.api.shared.controllers.dtos.QueueDtos.{AddTrackRequest, itemToQueueTrackResponse}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off public.methods.have.type
class CoinController @Inject()(
    cc: ControllerComponents,
    coinService: CoinService,
    queueService: QueueService,
    coinAuthAction: CoinActionBuilder)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def retrieveAllowedPlaylist() = coinAuthAction.async { request ⇒
    queueService.retrieveAllowedPlaylist(request.coin.venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case None ⇒ NoContent
    }
  }

  def retrieveRemainingUsages() = coinAuthAction.async { request ⇒
    coinService.retrieveRemainingUsages(request.coin).map(CoinRemainingUsagesResponse.apply)
  }

  def retrieveCurrentTrack() = coinAuthAction.async { request ⇒
    val eventualQueue = queueService.retrieveQueue(request.coin.venueUid)
    eventualQueue.map { queue ⇒
      queue.currentItem match {
        case Some(queueItem) ⇒ itemToQueueTrackResponse(queueItem)
        case None ⇒ NoContent
      }
    }
  }

  def addTrack() = coinAuthAction.async(parse.json[AddTrackRequest]) { request ⇒
    val triedIdentifier = TrackIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { trackIdentifier ⇒
      coinService.addTrack(request.coin, trackIdentifier).map(_ ⇒ NoContent)
    }
  }
}
