package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.dtos.Track.AddTrackRequest
import com.amplify.api.domain.logic.UserPlayerLogic
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.mvc.Controller
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class UserPlayerController @Inject()(
    userPlayerLogic: UserPlayerLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def addTrack(venueUid: String) = authenticatedUser(parse.json[AddTrackRequest]) { request ⇒
    ContentProviderIdentifier.fromString(request.body.identifier) match {
      case Success(trackIdentifier) ⇒
        userPlayerLogic.addTrack(venueUid, request.subject.user, trackIdentifier).map(_ ⇒ NoContent)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
