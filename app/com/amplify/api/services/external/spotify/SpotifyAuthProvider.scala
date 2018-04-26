package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.spotify.Converters.userToUserData
import com.amplify.api.services.external.spotify.Dtos.{RefreshAndAccessTokens, User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.services.external.spotify.SpotifyBaseClient._
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import play.api.http.Status
import play.api.libs.ws.{WSAuthScheme, WSResponse}
import scala.concurrent.{ExecutionContext, Future}

class SpotifyAuthProvider @Inject()(
    envConfig: EnvConfig,
    client: SpotifyBaseClient)(
    implicit ec: ExecutionContext) {

  val redirectUri = envConfig.spotifyRedirectUri
  val clientId = envConfig.spotifyClientId
  val clientSecret = envConfig.spotifyClientSecret

  def requestRefreshAndAccessTokens(authorizationCode: Token): Future[(Token, Token)] = {
    val body = Map(
      "grant_type" → Seq("authorization_code"),
      "code" → Seq(authorizationCode.value),
      "redirect_uri" → Seq(redirectUri),
      "client_id" → Seq(clientId),
      "client_secret" → Seq(clientSecret))

    client
      .accountsRequest("/api/token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .post(body)
      .flatMap(handleInvalidAuthorizationCode)
      .parseJson[RefreshAndAccessTokens]
      .map(tokens => (Token(tokens.refreshToken), Token(tokens.accessToken)))
  }

  private def handleInvalidAuthorizationCode(response: WSResponse): Future[WSResponse] = {
    if (response.status == Status.BAD_REQUEST &&
      (response.json \ "error").as[String] == "invalid_grant") {
      Future.failed(UserAuthTokenNotFound)
    }
    else Future.successful(response)
  }

  def fetchUser(accessToken: Token): Future[UserData] = {
    client
      .apiRequest("/me")
      .withBearerToken(accessToken)
      .get()
      .parseJson[SpotifyUser]
      .map(userToUserData)
  }
}
