package com.amplify.api.domain.queue

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieveAllowedPlaylist(venueUid: Uid): Future[Option[Playlist]]

  def setAllowedPlaylist(venueUid: Uid, playlistIdentifier: PlaylistIdentifier): Future[Unit]

  def retrieveQueue(venueUid: Uid): Future[Queue]

  def start(venueUid: Uid): Future[Unit]

  def skip(venueUid: Uid): Future[Unit]

  def finish(venueUid: Uid): Future[Unit]

  def addPlaylistTracks(venueUid: Uid, playlistIdentifier: PlaylistIdentifier): Future[Unit]

  def addVenueTrack(venueUid: Uid, trackIdentifier: TrackIdentifier): Future[Unit]

  def addTrack(venueUid: Uid, code: Code, trackIdentifier: TrackIdentifier): Future[Unit]
}
