package com.amplify.api.domain.playlist

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.{Access, Token}
import com.amplify.api.domain.models.{PlaylistIdentifier, PlaylistInfo, Track, TrackIdentifier}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[PlaylistExternalContentServiceImpl])
trait PlaylistExternalContentService {

  def fetchPlaylists(
      contentProvider: ContentProvider,
      accessToken: Token[Access]): Future[Seq[PlaylistInfo]]

  def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token[Access]): Future[PlaylistInfo]

  def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token[Access]): Future[Seq[Track]]

  def fetchTrack(trackIdentifier: TrackIdentifier, accessToken: Token[Access]): Future[Track]

  def startPlayback(tracks: Seq[TrackIdentifier], accessToken: Token[Access]): Future[Unit]
}
