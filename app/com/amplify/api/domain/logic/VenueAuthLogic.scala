package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue, VenueReq}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signUp(authToken: AuthToken, venueReq: VenueReq): Future[AuthenticatedVenue]
}
