package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
import com.amplify.api.exceptions.{UnexpectedResponseException, UserAuthTokenNotFound}
import com.amplify.api.services.external._
import com.amplify.api.services.external.spotify.Dtos.{Playlists, User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    val ws: WSClient,
    envConfig: EnvConfig)(
    implicit val ec: ExecutionContext) extends ContentProviderStrategy with SpotifyBaseClient {

  val baseUrl = envConfig.getString("spotify.web_api.url")

  override def fetchUser(implicit token: String): Future[UserData] = {
    spotifyGet[SpotifyUser]("/me").map { spotifyUser ⇒
      val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, spotifyUser.id)
      UserData(identifier, spotifyUser.displayName, spotifyUser.email)
    }
  }

  override def fetchPlaylists(implicit token: String): Future[Seq[PlaylistData]] = {
    spotifyGet[Playlists]("/me/playlists").map(_.items.map { item ⇒
      val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, item.id)
      PlaylistData(identifier, item.name)
    })
  }

  override def customHandleResponse(response: WSResponse): Future[JsValue] = {
    response.status match {
      case Http.Status.OK ⇒
        Future.successful(response.json)
      case Http.Status.UNAUTHORIZED ⇒
        Future.failed(UserAuthTokenNotFound)
      case other ⇒
        val message = s"Unexpected status $other from Spotify. " +
          s"Headers: ${response.allHeaders}. Body: ${response.body}"
        Future.failed(UnexpectedResponseException(message))
    }
  }
}
