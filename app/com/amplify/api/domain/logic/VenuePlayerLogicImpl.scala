package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthenticatedVenue
import com.amplify.api.domain.models.QueueCommand._
import com.amplify.api.domain.models.QueueEvent.{AllTracksRemoved, TrackFinished, CurrentTrackSkipped}
import com.amplify.api.services.{QueueEventService, QueueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    eventService: QueueEventService,
    queueService: QueueService)(
    implicit ec: ExecutionContext) extends VenuePlayerLogic {

  override def play(venue: AuthenticatedVenue): Future[Unit] = {
    eventService.create(StartPlayback(venue))
  }

  override def pause(venue: AuthenticatedVenue): Future[Unit] = {
    eventService.create(PausePlayback(venue))
  }

  override def skip(venue: AuthenticatedVenue): Future[Unit] = {
    for {
      _ ← eventService.create(SkipCurrentTrack(venue), CurrentTrackSkipped)
      _ ← queueService.update(venue.unauthenticated, CurrentTrackSkipped)
    }
    yield ()
  }

  override def startAmplifying(venue: AuthenticatedVenue): Future[Unit] = {
    eventService.create(StartAmplifying(venue))
  }

  override def stopAmplifying(venue: AuthenticatedVenue): Future[Unit] = {
    for {
      _ ← eventService.create(StopAmplifying(venue), AllTracksRemoved)
      _ ← queueService.update(venue.unauthenticated, AllTracksRemoved)
    }
    yield ()
  }

  override def trackFinished(venue: AuthenticatedVenue): Future[Unit] = {
    for {
      _ ← eventService.create(FinishTrack(venue), TrackFinished)
      _ ← queueService.update(venue.unauthenticated, TrackFinished)
    }
    yield ()
  }
}
