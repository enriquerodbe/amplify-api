package com.amplify.api.services.external

import com.amplify.api.configuration.EnvConfig
import com.github.tototoshi.play.json.JsonNaming
import javax.inject.Inject
import play.api.libs.json.{Format, JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.{ExecutionContext, Future}

class SpotifyAuthentication @Inject()(
    wsClient: WSClient,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends AuthenticationStrategy {

  implicit val responseFormat = JsonNaming.snakecase(Json.format[SpotifyAuthResponse])

  val baseUrl = envConfig.getString("spotify.web_api.url")

  override def fetchUser(token: String): Future[UserData] = {
    val request =
      wsClient
        .url(s"$baseUrl/me")
        .withHeaders(AUTHORIZATION → s"Bearer $token")
        .get()

    request.flatMap { response =>
      response.json.validate[SpotifyAuthResponse] match {
        case JsSuccess(data, _) ⇒
          Future.successful(UserData(data.id, data.displayName, data.email))
        case JsError(errors) ⇒
          Future.failed(new Exception(errors.toString()))
      }
    }
  }
}

case class SpotifyAuthResponse(id: String, displayName: String, email: String)
