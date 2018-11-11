package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{CoinToken, Queue, TrackIdentifier, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueLogicImpl])
trait QueueLogic {

  def retrieveQueue(venue: Venue): Future[Queue]

  def start(venue: Venue): Future[Unit]

  def skip(venue: Venue): Future[Unit]

  def finish(venue: Venue): Future[Unit]

  def addTrack(venueUid: Uid, coinToken: CoinToken, trackIdentifier: TrackIdentifier): Future[Unit]
}
