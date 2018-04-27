package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueQueueLogicImpl])
trait VenueQueueLogic {

  def retrieveQueue(venue: Venue): Future[Queue]

  def start(venue: Venue): Future[Unit]

  def skip(venue: Venue): Future[Unit]

  def finish(venue: Venue): Future[Unit]

  def addTrack(venueUid: Uid, coinToken: CoinToken, trackIdentifier: TrackIdentifier): Future[Unit]
}
