package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue}
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signUp(authToken: AuthToken, name: Name): Future[AuthenticatedVenue] = {
    for {
      userData ← authService.fetchUser(authToken)
      venue ← venueService.retrieveOrCreate(userData, name)
    }
    yield venue
  }
}
