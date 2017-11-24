package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.exceptions.{ExternalResourceNotFound, UnexpectedResponse, UserAuthTokenNotFound}
import com.amplify.api.utils.OAuthClient
import play.api.libs.ws.WSResponse
import play.mvc.Http
import scala.concurrent.Future

trait SpotifyBaseProvider extends OAuthClient {

  val envConfig: EnvConfig

  override lazy val baseUrl = envConfig.spotifyUrl

  override def customHandleResponse(response: WSResponse): Future[WSResponse] = {
    response.status match {
      case Http.Status.OK | Http.Status.NO_CONTENT ⇒
        Future.successful(response)
      case Http.Status.UNAUTHORIZED ⇒
        Future.failed(UserAuthTokenNotFound)
      case Http.Status.NOT_FOUND ⇒
        Future.failed(ExternalResourceNotFound)
      case other ⇒
        val message = s"Unexpected status $other from Spotify. " +
          s"Headers: ${response.allHeaders}. Body: ${response.body}"
        Future.failed(UnexpectedResponse(message))
    }
  }
}
