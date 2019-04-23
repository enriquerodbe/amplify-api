package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Refresh, Token}
import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.shared.services.external.models.{RefreshTokens, UserData}
import com.amplify.api.shared.services.external.spotify.SpotifyAuthProvider
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

private class VenueExternalAuthServiceImpl @Inject()(
    spotifyAuthProvider: SpotifyAuthProvider)(
    implicit ec: ExecutionContext) extends VenueExternalAuthService {

  override def requestRefreshAndAccessTokens(
      authorizationCode: AuthToken[AuthorizationCode]): Future[RefreshTokens] = {
    authorizationCode.authProvider match {
      case AuthProviderType.Spotify ⇒
        spotifyAuthProvider
          .requestRefreshAndAccessTokens(authorizationCode.token)
          .map(RefreshTokens.tupled)
    }
  }

  override def refreshAccessToken(
      refreshToken: AuthToken[Refresh]): Future[Token[Access]] = {
    refreshToken.authProvider match {
      case AuthProviderType.Spotify ⇒
        spotifyAuthProvider.refreshAccessToken(refreshToken.token)
    }
  }

  override def fetchUser(authToken: AuthToken[Access]): Future[UserData] = {
    authToken.authProvider match {
      case AuthProviderType.Spotify ⇒ spotifyAuthProvider.fetchUser(authToken.token)
    }
  }
}
