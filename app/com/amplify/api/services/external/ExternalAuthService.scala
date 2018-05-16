package com.amplify.api.services.external

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Refresh, Token}
import com.amplify.api.services.external.models.{RefreshTokens, UserData}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[ExternalAuthServiceImpl])
trait ExternalAuthService {

  def requestRefreshAndAccessTokens(
      authorizationCode: AuthToken[AuthorizationCode]): Future[RefreshTokens]

  def refreshAccessToken(refreshToken: AuthToken[Refresh]): Future[Token[Access]]

  def fetchUser(authToken: AuthToken[Access]): Future[UserData]
}
