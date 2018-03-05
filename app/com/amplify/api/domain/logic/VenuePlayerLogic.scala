package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentProviderIdentifier, User, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlayerLogicImpl])
trait VenuePlayerLogic {

  def play(venue: Venue): Future[Unit]

  def pause(venue: Venue): Future[Unit]

  def skip(venue: Venue): Future[Unit]

  def addTrack(venueUid: Uid, user: User, trackIdentifier: ContentProviderIdentifier): Future[Unit]
}
