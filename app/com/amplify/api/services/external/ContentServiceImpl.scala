package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContentServiceImpl @Inject()(
    spotifyContentProvider: SpotifyContentProvider)(
    implicit ec: ExecutionContext) extends ContentService {

  override def fetchPlaylists(
      contentProvider: ContentProvider,
      accessToken: Token): Future[Seq[PlaylistInfo]] = {
    contentProvider match {
      case ContentProvider.Spotify ⇒
        spotifyContentProvider.fetchPlaylists(accessToken).map(_.map(toModelPlaylist))
    }
  }

  override def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token): Future[PlaylistInfo] = {
    playlistIdentifier match {
      case spotifyUri: Spotify.PlaylistUri ⇒
        spotifyContentProvider.fetchPlaylist(spotifyUri, accessToken).map(toModelPlaylist)
    }
  }

  override def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token): Future[Seq[Track]] = {
    playlistIdentifier match {
      case spotifyUri: Spotify.PlaylistUri ⇒
        spotifyContentProvider
          .fetchPlaylistTracks(spotifyUri, accessToken)
          .map(_.map(toModelTrack))
    }
  }
}
