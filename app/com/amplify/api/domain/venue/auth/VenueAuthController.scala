package com.amplify.api.domain.venue.auth

import be.objectify.deadbolt.scala.ActionBuilders
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
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with VenueAuthRequests {

  def signIn = Action.async(parse.json[VenueSignInRequest]) { request ⇒
    val authorizationCode =
      AuthToken(AuthProviderType.Spotify, Token[AuthorizationCode](request.body.code))
    val eventualVenue = venueAuthService.signIn(authorizationCode)
    eventualVenue.map { venue ⇒
      venueToVenueResponse(venue).withSession(AuthHeaders.VENUE_UID → venue.uid.value)
    }
  }

  def retrieveCurrent() = authenticatedVenue() { request ⇒
    venueAuthService.refreshToken(request.subject.venue).map(venueToVenueResponse)
  }
}
