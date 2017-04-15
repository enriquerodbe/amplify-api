package com.amplify.api.services.external

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.{AuthProviderType, User}
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name}
import javax.inject.Inject
import scala.concurrent.Future

trait AuthenticationStrategy {

  def fetchUser(token: String): Future[Option[UserData]]
}

class AuthenticationStrategiesRegistry @Inject()(spotifyAuthentication: SpotifyAuthentication) {

  def getStrategy(authProvider: AuthProviderType): AuthenticationStrategy = authProvider match {
    case AuthProviderType.Spotify ⇒ spotifyAuthentication
  }
}

case class UserData(
    identifier: Identifier[User],
    name: Name[User],
    email: Email)
