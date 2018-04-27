package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.Spotify.PlaylistUri
import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.exceptions.{ExternalResourceNotFound, RequestedResourceNotFound}
import com.amplify.api.services.external.spotify.Dtos.{Playlist, TrackItem}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.services.external.spotify.SpotifyBaseClient._
import javax.inject.Inject
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    client: SpotifyBaseClient,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) {

  def fetchPlaylists(accessToken: Token): Future[Seq[Playlist]] = {
    client.paginatedFetch[Playlist]("/me/playlists", accessToken)
  }

  def fetchPlaylist(uri: PlaylistUri, accessToken: Token): Future[Playlist] = {
    client
      .apiRequest(s"/users/${uri.owner}/playlists/${uri.id}")
      .withBearerToken(accessToken)
      .get()
      .parseJson[Playlist]
      .recoverWith {
        case ExternalResourceNotFound ⇒
          Future.failed(RequestedResourceNotFound(uri.toString))
      }
  }

  def fetchPlaylistTracks(uri: PlaylistUri, accessToken: Token): Future[Seq[TrackItem]] = {
    val path = s"/users/${uri.owner}/playlists/${uri.id}/tracks"
    val query = Map("fields" → "next,total,items(track(id,name,album))")
    client
      .paginatedFetch[TrackItem](path, accessToken, query = query)
      .recoverWith {
        case ExternalResourceNotFound ⇒
          Future.failed(RequestedResourceNotFound(uri.toString))
      }
  }

  def startPlayback(tracks: Seq[TrackIdentifier], accessToken: Token): Future[Unit] = {
    client
      .apiRequest("/me/player/play")
      .withBearerToken(accessToken)
      .put(Json.obj("uris" → tracks.map(_.toString)))
      .emptyResponse
  }
}
