package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.{AuthHeadersUtil, AuthenticatedRequests}
import com.amplify.api.controllers.dtos.Queue.AddTrackRequest
import com.amplify.api.domain.logic.VenuePlayerLogic
import com.amplify.api.domain.models.{ContentIdentifier, TrackIdentifier}
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.exceptions.InvalidProviderIdentifier
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenuePlayerController @Inject()(
    cc: ControllerComponents,
    venuePlayerLogic: VenuePlayerLogic,
    authHeadersUtil: AuthHeadersUtil,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.skip(request.subject.venue).map(_ ⇒ NoContent)
  }

  def finish() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.finish(request.subject.venue).map(_ ⇒ NoContent)
  }

  def addTrack(uid: String) = authenticatedUser(parse.json[AddTrackRequest]) { request ⇒
    ContentIdentifier.fromString(request.body.identifier) match {
      case Success(identifier: TrackIdentifier) ⇒
        venuePlayerLogic.addTrack(Uid(uid), request.subject.user, identifier).map(_ ⇒ NoContent)
      case Success(otherIdentifier) ⇒
        Future.failed(InvalidProviderIdentifier(otherIdentifier.toString))
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
