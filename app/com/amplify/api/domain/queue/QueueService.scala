package com.amplify.api.domain.queue

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]]

  def setCurrentPlaylist(venue: Venue, playlistIdentifier: PlaylistIdentifier): Future[Unit]

  def retrieveQueue(venue: Venue): Future[Queue]

  def start(venue: Venue): Future[Unit]

  def skip(venue: Venue): Future[Unit]

  def finish(venue: Venue): Future[Unit]

  def addTrack(venueUid: Uid, coinCode: CoinCode, trackIdentifier: TrackIdentifier): Future[Unit]
}
