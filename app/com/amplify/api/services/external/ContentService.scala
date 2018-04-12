package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.{AuthToken, PlaylistIdentifier, PlaylistInfo, Track}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[ContentServiceImpl])
trait ContentService {

  def fetchPlaylists(
      contentProvider: ContentProvider)(
      implicit token: AuthToken): Future[Seq[PlaylistInfo]]

  def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier)(
      implicit authToken: AuthToken): Future[PlaylistInfo]

  def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier)(
      implicit token: AuthToken): Future[Seq[Track]]
}
