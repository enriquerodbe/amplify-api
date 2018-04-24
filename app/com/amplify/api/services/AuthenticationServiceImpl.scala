package com.amplify.api.services

import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.services.external.spotify.SpotifyAuthProvider
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import scala.concurrent.Future

class AuthenticationServiceImpl @Inject()(spotifyAuthProvider: SpotifyAuthProvider)
  extends AuthenticationService {

  override def requestRefreshAndAccessTokens(
      authorizationCode: AuthToken): Future[(Token, Token)] = {
    authorizationCode.authProvider match {
      case AuthProviderType.Spotify ⇒
        spotifyAuthProvider.requestRefreshAndAccessTokens(authorizationCode.token)
    }
  }

  override def fetchUser(implicit authToken: AuthToken): Future[UserData] = {
    authToken.authProvider match {
      case AuthProviderType.Spotify ⇒ spotifyAuthProvider.fetchUser
    }
  }
}
