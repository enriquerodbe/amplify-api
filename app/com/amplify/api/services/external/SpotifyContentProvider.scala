package com.amplify.api.services.external

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType, User}
import com.amplify.api.exceptions.SpotifyException
import com.github.tototoshi.play.json.JsonNaming
import javax.inject.Inject
import play.api.libs.json.{Format, JsError, JsSuccess, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    wsClient: WSClient,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends ContentProviderStrategy {

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
          val identifier = ContentProviderIdentifier[User](ContentProviderType.Spotify, data.id)
          Future.successful(Some(UserData(identifier, data.displayName, data.email)))
        case JsError(errors) ⇒
          Future.failed(SpotifyException(s"Spotify bad response: ${errors.toString})"))
      }
    }
  }

  implicit val playlistItemFormat: Format[SpotifyPlaylistItem] = {
    JsonNaming.snakecase(Json.format[SpotifyPlaylistItem])
  }
  implicit val playlistFormat: Format[SpotifyPlaylistsResponse] = {
    JsonNaming.snakecase(Json.format[SpotifyPlaylistsResponse])
  }

  override def fetchPlaylists(token: String): Future[Seq[PlaylistData]] = {
    val request =
      wsClient
        .url(s"$baseUrl/me/playlists")
        .withHeaders(AUTHORIZATION → s"Bearer $token")
        .get()

    request.map {
      _.json.validate[SpotifyPlaylistsResponse].get.items.map { item ⇒
        PlaylistData(ContentProviderIdentifier(ContentProviderType.Spotify, item.id), item.name)
      }
    }
  }
}

case class SpotifyAuthResponse(id: String, displayName: String, email: String)

case class SpotifyPlaylistsResponse(items: Seq[SpotifyPlaylistItem])

case class SpotifyPlaylistItem(id: String, name: String)
