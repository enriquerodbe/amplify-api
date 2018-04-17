package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.{AuthHeadersUtil, AuthenticatedRequests}
import com.amplify.api.controllers.dtos.SuccessfulResponse
import com.amplify.api.controllers.dtos.Venue._
import com.amplify.api.domain.logic.VenueAuthLogic
import com.amplify.api.domain.models.primitives.Name
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueAuthController @Inject()(
    cc: ControllerComponents,
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def signUp = Action.async(parse.json[VenueRequest]) { request ⇒
    authHeadersUtil.getAuthTokenFromHeaders(request) match {
      case Success(authToken) ⇒
        val venueName = Name(request.body.name)
        val eventualVenue = venueAuthLogic.signUp(authToken, venueName)
        eventualVenue.map(venue ⇒ SuccessfulResponse(venueToVenueResponse(venue)))
      case Failure(exception) ⇒
        Future.failed(exception)
    }
  }

  def retrieveCurrent() = authenticatedVenue() { request ⇒
    val response = venueToVenueResponse(request.subject.venue)
    Future.successful(SuccessfulResponse(response))
  }
}
