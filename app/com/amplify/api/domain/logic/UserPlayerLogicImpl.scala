package com.amplify.api.domain.logic

import com.amplify.api.domain.models.QueueCommand.AddUserTrack
import com.amplify.api.domain.models.QueueEvent.{UserTrackAdded ⇒ QueueAddUserTrack}
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.services.{QueueEventService, QueueService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserPlayerLogicImpl @Inject()(
    eventService: QueueEventService,
    queueService: QueueService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends UserPlayerLogic {

  override def addTrack(
      venueUid: Uid,
      user: AuthenticatedUser,
      identifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      venue ← venueService.retrieve(venueUid)
      queueCommand = AddUserTrack(venue, user, identifier)
      queueEvent = QueueAddUserTrack(user, identifier)
      _ ← eventService.create(queueCommand, queueEvent)
      _ ← queueService.update(venue.unauthenticated, queueEvent)
    }
    yield ()
  }
}
