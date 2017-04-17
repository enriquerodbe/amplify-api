package com.amplify.api.services.external

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.exceptions.SpotifyException
import com.github.tototoshi.play.json.JsonNaming
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.{ExecutionContext, Future}

class SpotifyAuthentication @Inject()(
    wsClient: WSClient,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends AuthenticationStrategy {

  implicit val responseFormat = JsonNaming.snakecase(Json.format[SpotifyAuthResponse])

  val baseUrl = envConfig.getString("spotify.web_api.url")

  override def fetchUser(token: String): Future[Option[UserData]] = {
    val request =
      wsClient
        .url(s"$baseUrl/me")
        .withHeaders(AUTHORIZATION → s"Bearer $token")
        .get()

    request.flatMap(handleResponse)
  }

  private def handleResponse(response: WSResponse) = {
    if (response.status == Http.Status.UNAUTHORIZED) {
      Future.successful(None)
    } else {
      response.json.validate[SpotifyAuthResponse] match {
        case JsSuccess(data, _) ⇒
          Future.successful(Some(UserData(data.id, data.displayName, data.email)))
        case JsError(errors) ⇒
          Future.failed(SpotifyException(s"Spotify bad response: ${errors.toString})"))
      }
    }
  }
}

case class SpotifyAuthResponse(id: String, displayName: String, email: String)
