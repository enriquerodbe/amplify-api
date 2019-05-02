package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.ActionBuilders
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
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with CoinAuthRequests {

  def retrieveAllowedPlaylist() = authenticatedCoin() { request ⇒
    queueService.retrieveAllowedPlaylist(request.subject.coin.venueUid).map {
      case Some(playlist) ⇒ playlistToPlaylistResponse(playlist)
      case None ⇒ NoContent
    }
  }

  def retrieveRemainingUsages() = authenticatedCoin() { request ⇒
    coinService.retrieveRemainingUsages(request.subject.coin).map(CoinRemainingUsagesResponse.apply)
  }

  def retrieveCurrentTrack() = authenticatedCoin() { request ⇒
    val eventualQueue = queueService.retrieveQueue(request.subject.coin.venueUid)
    eventualQueue.map { queue ⇒
      queue.currentItem match {
        case Some(queueItem) ⇒ itemToQueueTrackResponse(queueItem)
        case None ⇒ NoContent
      }
    }
  }

  def addTrack() = authenticatedCoin(parse.json[AddTrackRequest]) { request ⇒
    val triedIdentifier = TrackIdentifier.fromString(request.body.identifier)
    Future.fromTry(triedIdentifier).flatMap { trackIdentifier ⇒
      coinService.addTrack(request.subject.coin, trackIdentifier).map(_ ⇒ NoContent)
    }
  }
}
