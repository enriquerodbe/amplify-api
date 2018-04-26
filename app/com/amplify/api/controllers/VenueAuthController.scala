package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.{AuthHeadersUtil, AuthenticatedRequests}
import com.amplify.api.controllers.dtos.Venue._
import com.amplify.api.domain.logic.VenueAuthLogic
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.services.AuthenticationService
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off public.methods.have.type
class VenueAuthController @Inject()(
    cc: ControllerComponents,
    venueAuthLogic: VenueAuthLogic,
    authenticationService: AuthenticationService,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def signIn = Action.async(parse.json[VenueSignInRequest]) { request ⇒
    val authorizationCode = AuthToken(AuthProviderType.Spotify, Token(request.body.code))
    val eventualVenue = venueAuthLogic.signIn(authorizationCode)
    eventualVenue.map { venue ⇒
      venueToVenueResponse(venue).withSession(AuthHeadersUtil.VENUE_UID → venue.uid.value)
    }
  }

  def retrieveCurrent() = authenticatedVenue() { request ⇒
    Future.successful(venueToVenueResponse(request.subject.venue))
  }
}
