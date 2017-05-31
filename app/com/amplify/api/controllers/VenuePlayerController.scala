package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.logic.VenuePlayerLogic
import com.amplify.api.utils.AuthenticatedRequests
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
    implicit val authToken = request.authToken
    venuePlayerLogic.play(request.subject.venue).map(_ ⇒ NoContent)
  }

  def pause() = authenticatedVenue(parse.empty) { request ⇒
    implicit val authToken = request.authToken
    venuePlayerLogic.pause(request.subject.venue).map(_ ⇒ NoContent)
  }

  def startAmplifying() = authenticatedVenue(parse.empty) { request ⇒
    implicit val authToken = request.authToken
    venuePlayerLogic.startAmplifying(request.subject.venue).map(_ ⇒ NoContent)
  }

  def stopAmplifying() = authenticatedVenue(parse.empty) { request ⇒
    implicit val authToken = request.authToken
    venuePlayerLogic.stopAmplifying(request.subject.venue).map(_ ⇒ NoContent)
  }
}
