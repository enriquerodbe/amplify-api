package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.Spotify.PlaylistUri
import com.amplify.api.exceptions.{ExternalResourceNotFound, RequestedResourceNotFound}
import com.amplify.api.services.external.spotify.Dtos.{Playlist, TrackItem}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.utils.Pagination
import javax.inject.Inject
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    override val ws: WSClient,
    override val envConfig: EnvConfig)(
    override implicit val ec: ExecutionContext)
  extends SpotifyBaseProvider with Pagination {

  override val itemsField = "items"
  override val totalField = "total"
  override val nextField = "next"
  override val paginationOffsetHeader = "offset"

  def fetchPlaylists(implicit token: AuthToken): Future[Seq[Playlist]] = {
    paginatedFetch[Playlist]("/me/playlists")
  }

  def fetchPlaylist(
      uri: PlaylistUri)(
      implicit authToken: AuthToken): Future[Playlist] = {
    apiGet[Playlist](s"/users/${uri.owner}/playlists/${uri.id}")
      .recoverWith {
        case ExternalResourceNotFound ⇒
          Future.failed(RequestedResourceNotFound(uri.toString))
      }
  }

  def fetchPlaylistTracks(
      uri: PlaylistUri)(
      implicit token: AuthToken): Future[Seq[TrackItem]] = {
    val path = s"/users/${uri.owner}/playlists/${uri.id}/tracks"
    val query = Map("fields" → "next,total,items(track(id,name,album))")
    paginatedFetch[TrackItem](path, query)
      .recoverWith {
        case ExternalResourceNotFound ⇒
          Future.failed(RequestedResourceNotFound(uri.toString))
      }
  }
}
