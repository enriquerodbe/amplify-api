package com.amplify.api.domain.playlist

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Access, Token}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PlaylistServiceImpl @Inject()(
    contentService: PlaylistExternalContentService)(
    implicit ec: ExecutionContext) extends PlaylistService {

  override def retrievePlaylists(
      venue: Venue)(
      accessToken: Token[Access]): Future[Seq[PlaylistInfo]] = {
    contentService.fetchPlaylists(venue.contentProviders, accessToken)
  }

  override def retrievePlaylist(
      identifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[Playlist] = {
    val eventualPlaylistInfo = retrievePlaylistInfo(identifier)(accessToken)
    val eventualPlaylistTracks = retrievePlaylistTracks(identifier)(accessToken)
    for {
      playlistInfo ← eventualPlaylistInfo
      playlistTracks ← eventualPlaylistTracks
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  private def retrievePlaylistInfo(
      playlistIdentifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[PlaylistInfo] = {
    contentService.fetchPlaylist(playlistIdentifier, accessToken)
  }

  private def retrievePlaylistTracks(
      playlistIdentifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[Seq[Track]] = {
    contentService.fetchPlaylistTracks(playlistIdentifier, accessToken)
  }
}
