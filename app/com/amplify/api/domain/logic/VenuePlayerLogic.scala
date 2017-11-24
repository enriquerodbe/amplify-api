package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{AuthenticatedVenueReq, ContentProviderIdentifier, User}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlayerLogicImpl])
trait VenuePlayerLogic {

  def play(venue: AuthenticatedVenueReq): Future[Unit]

  def pause(venue: AuthenticatedVenueReq): Future[Unit]

  def skip(venue: AuthenticatedVenueReq): Future[Unit]

  def addTrack(uid: Uid, user: User, trackIdentifier: ContentProviderIdentifier): Future[Unit]
}
