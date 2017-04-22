package com.amplify.api.domain.logic

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signUp(
      authProviderType: ContentProviderType,
      authToken: String,
      name: Name[Venue]): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authProviderType, authToken)
      creationResult ← venueService.create(name, userData, authProviderType)
    } yield creationResult
  }
}
