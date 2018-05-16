package com.amplify.api.services

import com.amplify.api.domain.models.primitives.AuthorizationCode
import com.amplify.api.domain.models.{AuthToken, VenueData}
import com.amplify.api.services.external.ExternalAuthService
import com.amplify.api.services.external.models.RefreshTokens
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthServiceImpl @Inject()(
    authService: ExternalAuthService)(
    implicit ec: ExecutionContext) extends VenueAuthService {

  override def signIn(authCode: AuthToken[AuthorizationCode]): Future[VenueData] = {
    for {
      RefreshTokens(refreshToken, accessToken) ← authService.requestRefreshAndAccessTokens(authCode)
      userData ← authService.fetchUser(AuthToken(authCode.authProvider, accessToken))
    }
    yield VenueData(userData.identifier, userData.name, refreshToken, accessToken)
  }
}
