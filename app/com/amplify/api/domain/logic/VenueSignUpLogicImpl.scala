package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueSignUpLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueSignUpLogic {

  override def signUp(
      name: Name[Venue],
      authProviderType: AuthProviderType,
      authToken: String): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authProviderType, authToken)
      creationResult ← venueService.create(name, userData, authProviderType)
    } yield creationResult
  }
}
