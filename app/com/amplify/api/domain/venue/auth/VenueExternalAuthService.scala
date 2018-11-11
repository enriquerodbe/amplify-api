package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Refresh, Token}
import com.amplify.api.shared.services.external.models.{RefreshTokens, UserData}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueExternalAuthServiceImpl])
trait VenueExternalAuthService {

  def requestRefreshAndAccessTokens(
      authorizationCode: AuthToken[AuthorizationCode]): Future[RefreshTokens]

  def refreshAccessToken(refreshToken: AuthToken[Refresh]): Future[Token[Access]]

  def fetchUser(authToken: AuthToken[Access]): Future[UserData]
}
