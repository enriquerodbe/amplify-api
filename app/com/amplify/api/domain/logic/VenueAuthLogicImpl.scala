package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, Venue}
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signUp(authToken: AuthToken, name: Name): Future[Venue] = {
    for {
      userData ← authService.fetchUser(authToken)
      venue ← venueService.getOrCreate(userData, name)
    }
    yield venue
  }
}
