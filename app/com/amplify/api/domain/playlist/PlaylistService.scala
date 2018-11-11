package com.amplify.api.domain.playlist

import com.amplify.api.domain.models.primitives.{Access, Token}
import com.amplify.api.domain.models.{Playlist, PlaylistIdentifier, PlaylistInfo, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[PlaylistServiceImpl])
trait PlaylistService {

  def retrievePlaylists(venue: Venue)(accessToken: Token[Access]): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(identifier: PlaylistIdentifier)(accessToken: Token[Access]): Future[Playlist]
}
