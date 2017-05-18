package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.{Track ⇒ ModelTrack}
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.exceptions.{UnexpectedResponseException, UserAuthTokenNotFound}
import com.amplify.api.services.external._
import com.amplify.api.services.external.models.{PlaylistData, TrackData, UserData}
import com.amplify.api.services.external.spotify.Converters.{playlistToPlaylistData, trackItemToTrackData, userToUserData}
import com.amplify.api.services.external.spotify.Dtos.{Playlists, Track, TrackItem, User ⇒ SpotifyUser}
import com.amplify.api.services.external.spotify.JsonConverters._
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    val ws: WSClient,
    envConfig: EnvConfig)(
    implicit val ec: ExecutionContext) extends ContentProviderStrategy with SpotifyBaseClient {

  val baseUrl = envConfig.getString("spotify.web_api.url")

  override def fetchUser(implicit token: String): Future[UserData] = {
    spotifyGet[SpotifyUser]("/me").map(userToUserData)
  }

  override def fetchPlaylists(implicit token: String): Future[Seq[PlaylistData]] = {
    spotifyGet[Playlists]("/me/playlists").map(_.items.map(playlistToPlaylistData))
  }

  override def fetchPlaylistTracks(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit token: String): Future[Seq[TrackData]] = {
    val path = s"/users/$userIdentifier/playlists/$playlistIdentifier/tracks"
    val query = Map("fields" → "next,total,items(track(id,name))")
    paginatedFetch[TrackItem](path, query, Seq.empty, 0).map(_.map(trackItemToTrackData))
  }

  override def play(
      userIdentifier: Identifier,
      tracks: Seq[ModelTrack])(
      implicit token: AuthToken): Future[Unit] = {
    val uris = tracks.map(t ⇒ s"spotify:track:${t.contentProviderIdentifier.identifier}")
    spotifyPut("/me/player/play", Json.obj("uris" → uris))(token.token).map(_ ⇒ ())
  }

  override def customHandleResponse(response: WSResponse): Future[WSResponse] = {
    response.status match {
      case Http.Status.OK | Http.Status.NO_CONTENT ⇒
        Future.successful(response)
      case Http.Status.UNAUTHORIZED ⇒
        Future.failed(UserAuthTokenNotFound)
      case other ⇒
        val message = s"Unexpected status $other from Spotify. " +
          s"Headers: ${response.allHeaders}. Body: ${response.body}"
        Future.failed(UnexpectedResponseException(message))
    }
  }
}
