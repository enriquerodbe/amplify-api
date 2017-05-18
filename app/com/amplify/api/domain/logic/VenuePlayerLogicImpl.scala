package com.amplify.api.domain.logic
import com.amplify.api.domain.models.EventSource.StartPlaying
import com.amplify.api.domain.models.QueueEvent.{StartPlaying ⇒ QueueStartPlaying}
import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue}
import com.amplify.api.services.{EventService, PlayerService, QueueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    eventService: EventService,
    queueService: QueueService,
    playerService: PlayerService)(
    implicit ec: ExecutionContext) extends VenuePlayerLogic {

  override def play(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit] = {
    for {
      _ ← eventService.create(StartPlaying(venue), QueueStartPlaying)
      queue ← queueService.update(venue.toUnauthenticated, QueueStartPlaying)
      _ ← playerService.play(venue, queue)
    }
    yield ()
  }
}
