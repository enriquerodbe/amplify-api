package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.primitives.{AuthorizationCode, Token}
import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.shared.controllers.dtos.VenueDtos.{VenueSignInRequest, venueToVenueResponse}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class VenueAuthController @Inject()(
    cc: ControllerComponents,
    venueAuthService: VenueAuthService,
    venueActionBuilder: VenueActionBuilder)(
    implicit ec: ExecutionContext) extends AbstractController(cc) {

  def signIn = Action.async(parse.json[VenueSignInRequest]) { request ⇒
    val authorizationCode =
      AuthToken(AuthProviderType.Spotify, Token[AuthorizationCode](request.body.code))
    val eventualVenue = venueAuthService.signIn(authorizationCode)
    eventualVenue.map { venue ⇒
      venueToVenueResponse(venue).withSession(VenueAuthenticatedBuilder.VENUE_UID → venue.uid.value)
    }
  }

  def retrieveCurrent() = venueActionBuilder.async { request ⇒
    venueAuthService.refreshToken(request.venue).map(venueToVenueResponse)
  }
}
