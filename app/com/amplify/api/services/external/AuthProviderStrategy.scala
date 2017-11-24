package com.amplify.api.services.external

import com.amplify.api.domain.models.AuthProviderType.{AuthProviderType, Spotify}
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.services.models.UserData
import com.amplify.api.services.external.spotify.SpotifyAuthProvider
import javax.inject.Inject
import scala.concurrent.Future

trait AuthProviderStrategy {

  def fetchUser(implicit token: AuthToken): Future[UserData]
}

class AuthProviderRegistry @Inject()(spotifyAuthProvider: SpotifyAuthProvider) {

  def getStrategy(authProvider: AuthProviderType): AuthProviderStrategy = {
    authProvider match {
      case Spotify ⇒ spotifyAuthProvider.asInstanceOf[AuthProviderStrategy]
    }
  }
}
