package com.amplify.api.domain.queue

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieveCurrentPlaylist(venueUid: Uid): Future[Option[Playlist]]

  def setCurrentPlaylist(venueUid: Uid, playlistIdentifier: PlaylistIdentifier): Future[Unit]

  def retrieveQueue(venueUid: Uid): Future[Queue]

  def start(venueUid: Uid): Future[Unit]

  def skip(venueUid: Uid): Future[Unit]

  def finish(venueUid: Uid): Future[Unit]

  def addTrack(venueUid: Uid, coinCode: CoinCode, trackIdentifier: TrackIdentifier): Future[Unit]
}
