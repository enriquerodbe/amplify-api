package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.{AuthHeadersUtil, AuthenticatedRequests}
import com.amplify.api.controllers.dtos.Queue.AddTrackRequest
import com.amplify.api.domain.logic.VenuePlayerLogic
import com.amplify.api.domain.models.ContentProviderIdentifier
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

  def play() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.play(request.subject.venueReq).map(_ ⇒ NoContent)
  }

  def pause() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.pause(request.subject.venueReq).map(_ ⇒ NoContent)
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.skip(request.subject.venueReq).map(_ ⇒ NoContent)
  }

  def addTrack(uid: String) = authenticatedUser(parse.json[AddTrackRequest]) { request ⇒
    ContentProviderIdentifier.fromString(request.body.identifier) match {
      case Success(identifier) ⇒
        venuePlayerLogic.addTrack(uid, request.subject.user, identifier).map(_ ⇒ NoContent)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
