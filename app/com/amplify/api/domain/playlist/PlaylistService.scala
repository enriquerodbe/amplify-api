package com.amplify.api.domain.playlist

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Playlist, PlaylistIdentifier, PlaylistInfo}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[PlaylistServiceImpl])
trait PlaylistService {

  def retrievePlaylists(venueUid: Uid): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(venueUid: Uid, identifier: PlaylistIdentifier): Future[Playlist]
}
