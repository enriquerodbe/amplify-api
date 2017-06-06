package com.amplify.api.domain.logic

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signUp(authToken: AuthToken, venueReq: VenueRequest): Future[AuthenticatedVenue]
}
