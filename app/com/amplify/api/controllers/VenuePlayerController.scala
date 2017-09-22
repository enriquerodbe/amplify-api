package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.{AuthHeadersUtil, AuthenticatedRequests}
import com.amplify.api.domain.logic.VenuePlayerLogic
import javax.inject.Inject
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class VenuePlayerController @Inject()(
    venuePlayerLogic: VenuePlayerLogic,
    authHeadersUtil: AuthHeadersUtil,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def play() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.play(request.subject.venueReq).map(_ ⇒ NoContent)
  }

  def pause() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.pause(request.subject.venueReq).map(_ ⇒ NoContent)
  }

  def skip() = authenticatedVenue(parse.empty) { request ⇒
    venuePlayerLogic.skip(request.subject.venueReq).map(_ ⇒ NoContent)
  }
}
