package com.amplify.api.domain.logic

import com.amplify.api.domain.models.EventSource.AddUserTrack
import com.amplify.api.domain.models.QueueEvent.{AddUserTrack ⇒ QueueAddUserTrack}
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.services.{EventService, QueueService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserPlayerLogicImpl @Inject()(
    eventService: EventService,
    queueService: QueueService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends UserPlayerLogic {

  override def addTrack(
      venueUid: Uid,
      user: AuthenticatedUser,
      identifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      venue ← venueService.retrieve(venueUid)
      eventSource = AddUserTrack(venue, user, identifier)
      queueEvent = QueueAddUserTrack(user, identifier)
      _ ← eventService.create(eventSource, queueEvent)
      _ ← queueService.update(venue.unauthenticated, queueEvent)
    }
    yield ()
  }
}
