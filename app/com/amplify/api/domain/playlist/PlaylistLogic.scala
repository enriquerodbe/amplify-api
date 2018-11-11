package com.amplify.api.domain.playlist

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Playlist, PlaylistIdentifier, PlaylistInfo, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[PlaylistLogicImpl])
trait PlaylistLogic {

  def retrievePlaylists(user: Venue): Future[Seq[PlaylistInfo]]

  def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]]

  def setCurrentPlaylist(venue: Venue, playlistIdentifier: PlaylistIdentifier): Future[Unit]
}
