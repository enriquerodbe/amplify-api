package com.amplify.api.domain.logic

import com.amplify.api.domain.models.EventSource.{PausePlayback, StartPlayback}
import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue}
import com.amplify.api.services.{EventService, QueueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    eventService: EventService,
    queueService: QueueService)(
    implicit ec: ExecutionContext) extends VenuePlayerLogic {

  override def play(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit] = {
    eventService.create(StartPlayback(venue))
  }

  override def pause(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit] = {
    eventService.create(PausePlayback(venue))
  }
}
