package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.services.external.spotify.Converters.userToUserData
import com.amplify.api.services.external.spotify.Dtos.{RefreshAndAccessTokens, User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}

class SpotifyAuthProvider @Inject()(
    override val ws: WSClient,
    override val envConfig: EnvConfig)(
    implicit val ec: ExecutionContext) extends SpotifyBaseProvider {

  override lazy val baseUrl = envConfig.spotifyWebApiUrl
  lazy val accountsUrl = envConfig.spotifyAccountsUrl
  lazy val redirectUri = envConfig.spotifyRedirectUri
  lazy val clientId = envConfig.spotifyClientId
  lazy val clientSecret = envConfig.spotifyClientSecret

  def requestRefreshAndAccessTokens(authorizationCode: Token): Future[(Token, Token)] = {
    val body = Map(
      "grant_type" → Seq("authorization_code"),
      "code" → Seq(authorizationCode.value),
      "redirect_uri" → Seq(redirectUri),
      "client_id" → Seq(clientId),
      "client_secret" → Seq(clientSecret))

    apiPostFormData[RefreshAndAccessTokens]("/api/token", body)
      .map(tokens ⇒ (Token(tokens.refreshToken), Token(tokens.accessToken)))
  }

  def fetchUser(implicit token: AuthToken): Future[UserData] = {
    apiGet[SpotifyUser]("/me").map(userToUserData)
  }
}
