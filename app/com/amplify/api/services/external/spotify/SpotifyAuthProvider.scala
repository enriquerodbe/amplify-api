package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Refresh, Token}
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.models.UserData
import com.amplify.api.services.external.spotify.Converters.userToUserData
import com.amplify.api.services.external.spotify.Dtos.{AccessToken, RefreshAndAccessTokens, User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.services.external.spotify.SpotifyBaseClient._
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

  def requestRefreshAndAccessTokens(
      authorizationCode: Token[AuthorizationCode]): Future[(Token[Refresh], Token[Access])] = {
    val body = Map(
      "grant_type" → Seq("authorization_code"),
      "code" → Seq(authorizationCode.value),
      "redirect_uri" → Seq(redirectUri))

    client
      .accountsRequest("/api/token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .post(body)
      .flatMap(handleInvalidAuthorizationCode)
      .parseJson[RefreshAndAccessTokens]
      .map(tokens => (Token(tokens.refreshToken), Token(tokens.accessToken)))
  }

  def refreshAccessToken(refreshToken: Token[Refresh]): Future[Token[Access]] = {
    val body = Map(
      "grant_type" → Seq("refresh_token"),
      "refresh_token" → Seq(refreshToken.value))

    client
      .accountsRequest("/api/token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .post(body)
      .parseJson[AccessToken]
      .map(t ⇒ Token(t.accessToken))
  }

  private def handleInvalidAuthorizationCode(response: WSResponse): Future[WSResponse] = {
    if (response.status == Status.BAD_REQUEST &&
      (response.json \ "error").as[String] == "invalid_grant") {
      Future.failed(UserAuthTokenNotFound)
    }
    else Future.successful(response)
  }

  def fetchUser(accessToken: Token[Access]): Future[UserData] = {
    client
      .apiRequest("/me")
      .withBearerToken(accessToken)
      .get()
      .parseJson[SpotifyUser]
      .map(userToUserData)
  }
}
