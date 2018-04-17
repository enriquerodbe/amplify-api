package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.Queue.{AddTrackRequest, queueToQueueResponse}
import com.amplify.api.controllers.dtos.SuccessfulResponse
import com.amplify.api.domain.logic.VenueQueueLogic
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentIdentifier, TrackIdentifier}
import com.amplify.api.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueQueueController @Inject()(
    cc: ControllerComponents,
    venueQueueLogic: VenueQueueLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def retrieveQueue() = authenticatedVenue() { request ⇒
    venueQueueLogic.retrieveQueue(request.subject.venue).map { queue ⇒
      SuccessfulResponse(queueToQueueResponse(queue))
    }
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    venueQueueLogic.skip(request.subject.venue).map(_ ⇒ NoContent)
  }

  def finish() = authenticatedVenue(parse.empty) { request ⇒
    venueQueueLogic.finish(request.subject.venue).map(_ ⇒ NoContent)
  }

  def addTrack(uid: String) = authenticatedUser(parse.json[AddTrackRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: TrackIdentifier) ⇒
        venueQueueLogic.addTrack(Uid(uid), request.subject.user, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
