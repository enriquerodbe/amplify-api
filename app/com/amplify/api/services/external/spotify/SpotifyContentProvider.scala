package com.amplify.api.services.external.spotify

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.services.external._
import com.amplify.api.services.models.{PlaylistData, TrackData}
import com.amplify.api.services.external.spotify.Converters.{playlistToPlaylistData, trackItemToTrackData}
import com.amplify.api.services.external.spotify.Dtos.{Playlist, Playlists, TrackItem}
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.utils.Pagination
import javax.inject.Inject
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}

class SpotifyContentProvider @Inject()(
    override val ws: WSClient,
    override val envConfig: EnvConfig)(
    override implicit val ec: ExecutionContext)
  extends ContentProviderStrategy with SpotifyBaseProvider with Pagination {

  override val itemsField = "items"
  override val totalField = "total"
  override val nextField = "next"
  override val paginationOffsetHeader = "offset"

  override def fetchPlaylists(implicit token: AuthToken): Future[Seq[PlaylistData]] = {
    apiGet[Playlists]("/me/playlists").map(_.items.map(playlistToPlaylistData))
  }

  override def fetchPlaylist(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit authToken: AuthToken): Future[PlaylistData] = {
    val playlist = apiGet[Playlist](s"/users/$userIdentifier/playlists/$playlistIdentifier")
    playlist.map(playlistToPlaylistData)
  }

  override def fetchPlaylistTracks(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit token: AuthToken): Future[Seq[TrackData]] = {
    val path = s"/users/$userIdentifier/playlists/$playlistIdentifier/tracks"
    val query = Map("fields" → "next,total,items(track(id,name,album))")
    val eventualItems = paginatedFetch[TrackItem](path, query, Seq.empty, 0)
    eventualItems.map(_.map(trackItemToTrackData))
  }
}
