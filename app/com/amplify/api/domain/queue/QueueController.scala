package com.amplify.api.domain.queue

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinAuthRequests
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, TrackIdentifier}
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.QueueDtos._
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class QueueController @Inject()(
    cc: ControllerComponents,
    queueLogic: QueueLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with VenueAuthRequests with CoinAuthRequests {

  def retrieveQueue() = authenticatedVenue(parse.empty) { request ⇒
    queueLogic.retrieveQueue(request.subject.venue).map(queueToQueueResponse)
  }

  def start() = authenticatedVenue(parse.empty) { request ⇒
    queueLogic.start(request.subject.venue).map(_ ⇒ NoContent)
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    queueLogic.skip(request.subject.venue).map(_ ⇒ NoContent)
  }

  def finish() = authenticatedVenue(parse.empty) { request ⇒
    queueLogic.finish(request.subject.venue).map(_ ⇒ NoContent)
  }

  def addTrack(uid: String) = authenticatedCoin(parse.json[AddTrackRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: TrackIdentifier) ⇒
        queueLogic
          .addTrack(Uid(uid), request.subject.coin.token, identifier)
          .map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
