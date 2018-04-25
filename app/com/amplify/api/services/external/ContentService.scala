package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.{AuthToken, PlaylistIdentifier, PlaylistInfo, Track}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[ContentServiceImpl])
trait ContentService {

  def fetchPlaylists(
      contentProvider: ContentProvider,
      accessToken: AuthToken): Future[Seq[PlaylistInfo]]

  def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: AuthToken): Future[PlaylistInfo]

  def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: AuthToken): Future[Seq[Track]]
}
