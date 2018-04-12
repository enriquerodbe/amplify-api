package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{TrackIdentifier, User, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlayerLogicImpl])
trait VenuePlayerLogic {

  def skip(venue: Venue): Future[Unit]

  def finish(venue: Venue): Future[Unit]

  def addTrack(venueUid: Uid, user: User, trackIdentifier: TrackIdentifier): Future[Unit]
}
