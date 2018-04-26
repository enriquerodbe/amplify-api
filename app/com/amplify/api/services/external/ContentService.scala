package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.domain.models.{PlaylistIdentifier, PlaylistInfo, Track}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[ContentServiceImpl])
trait ContentService {

  def fetchPlaylists(
      contentProvider: ContentProvider,
      accessToken: Token): Future[Seq[PlaylistInfo]]

  def fetchPlaylist(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token): Future[PlaylistInfo]

  def fetchPlaylistTracks(
      playlistIdentifier: PlaylistIdentifier,
      accessToken: Token): Future[Seq[Track]]
}
