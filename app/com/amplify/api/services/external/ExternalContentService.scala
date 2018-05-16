package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.{Access, Token}
import com.amplify.api.domain.models.{PlaylistIdentifier, PlaylistInfo, Track, TrackIdentifier}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[ExternalContentServiceImpl])
trait ExternalContentService {

  def fetchPlaylists(
      contentProvider: ContentProvider,
      accessToken: Token[Access]): Future[Seq[PlaylistInfo]]

  def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token[Access]): Future[PlaylistInfo]

  def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token[Access]): Future[Seq[Track]]

  def startPlayback(tracks: Seq[TrackIdentifier], accessToken: Token[Access]): Future[Unit]
}
