package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthenticatedVenue
import com.amplify.api.domain.models.EventSource._
import com.amplify.api.domain.models.QueueEvent.{RemoveAllTracks, TrackFinished ⇒ QueueTrackFinished, SkipCurrentTrack ⇒ QueueSkipCurrentTrack}
import com.amplify.api.services.{EventService, QueueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    eventService: EventService,
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
      _ ← eventService.create(SkipCurrentTrack(venue), QueueSkipCurrentTrack)
      _ ← queueService.update(venue.unauthenticated, QueueSkipCurrentTrack)
    }
    yield ()
  }

  override def startAmplifying(venue: AuthenticatedVenue): Future[Unit] = {
    eventService.create(StartAmplifying(venue))
  }

  override def stopAmplifying(venue: AuthenticatedVenue): Future[Unit] = {
    for {
      _ ← eventService.create(StopAmplifying(venue), RemoveAllTracks)
      _ ← queueService.update(venue.unauthenticated, RemoveAllTracks)
    }
    yield ()
  }

  override def trackFinished(venue: AuthenticatedVenue): Future[Unit] = {
    for {
      _ ← eventService.create(TrackFinished(venue), QueueTrackFinished)
      _ ← queueService.update(venue.unauthenticated, QueueTrackFinished)
    }
    yield ()
  }
}
