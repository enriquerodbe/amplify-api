package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.services.external._
import com.amplify.api.services.models.UserData
import com.amplify.api.services.external.spotify.Converters.userToUserData
import com.amplify.api.services.external.spotify.Dtos.{User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import javax.inject.Inject
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}

class SpotifyAuthProvider @Inject()(
    override val ws: WSClient,
    override val envConfig: EnvConfig)(
    implicit val ec: ExecutionContext) extends AuthProviderStrategy with SpotifyBaseProvider {

  override lazy val baseUrl = envConfig.spotifyUrl

  override def fetchUser(implicit token: AuthToken): Future[UserData] = {
    apiGet[SpotifyUser]("/me").map(userToUserData)
  }
}
