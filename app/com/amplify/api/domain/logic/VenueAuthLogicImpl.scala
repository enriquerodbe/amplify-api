package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signUp(
      authToken: AuthToken,
      name: Name): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authToken)
      creationResult ← venueService.create(userData, name)
    }
    yield creationResult
  }
}
