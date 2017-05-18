package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.logic.VenuePlayerLogic
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.mvc.Controller
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenuePlayerController @Inject()(
    venuePlayerLogic: VenuePlayerLogic,
    authHeadersUtil: AuthHeadersUtil,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def play() = authenticatedVenue(parse.empty) { request ⇒
    authHeadersUtil.getAuthToken(request) match {
      case Success(authToken) ⇒
        venuePlayerLogic.play(request.subject.venue)(authToken).map(_ ⇒ NoContent)
      case Failure(exception) ⇒
        Future.failed(exception)
    }
  }
}
