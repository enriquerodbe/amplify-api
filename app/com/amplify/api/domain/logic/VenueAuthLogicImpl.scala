package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{AuthToken, Venue}
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signIn(authorizationCode: AuthToken): Future[Venue] = {
    for {
      (refreshToken, accessToken) ← authService.requestRefreshAndAccessTokens(authorizationCode)
      venueData ← authService.fetchUser(AuthToken(authorizationCode.authProvider, accessToken))
      venue ← venueService.retrieveOrCreate(venueData, refreshToken, accessToken)
    }
    yield venue
  }

  override def login(venueUid: Uid): Future[Option[Venue]] = venueService.retrieve(venueUid)
}
