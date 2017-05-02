package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.controllers.dtos.Venue.SignUpReq
import com.amplify.api.controllers.dtos.Venue._
import com.amplify.api.domain.logic.VenueAuthLogic
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueAuthController @Inject()(
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp = Action.async(parse.json[SignUpReq]) { request ⇒
    authHeadersUtil.getAuthToken(request) match {
      case Success(authToken) ⇒
        val eventualVenue = venueAuthLogic.signUp(authToken, request.body.name)
        eventualVenue.map(venue ⇒ Ok(Json.toJson(venueToVenueResponse(venue))))
      case Failure(exception) ⇒
        Future.failed(exception)
    }
  }
}
