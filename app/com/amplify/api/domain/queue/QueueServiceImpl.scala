package com.amplify.api.domain.queue

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.queue.Command._
import com.amplify.api.domain.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.FutureUtils._
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class QueueServiceImpl @Inject()(
    envConfig: EnvConfig,
    venueService: VenueService,
    playlistService: PlaylistService,
    @Named("queue-command-router") queueCommandRouter: ActorRef)(
    implicit ec: ExecutionContext)
  extends QueueService {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]] = {
    for {
      venue ← venueService.retrieve(uid) ?! VenueNotFoundByUid(uid)
      queue ← (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
    }
    yield queue.currentPlaylist
  }

  override def setCurrentPlaylist(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[Unit] = {
    val command = SetCurrentPlaylist(venue, playlistIdentifier)
    (queueCommandRouter ? RouteCommand(command)).mapTo[Unit]
  }

  override def retrieveQueue(venue: Venue): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
  }

  override def start(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(StartPlayback(venue))).mapTo[Unit]
  }

  override def skip(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(SkipCurrentTrack(venue))).mapTo[Unit]
  }

  override def finish(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(FinishCurrentTrack(venue))).mapTo[Unit]
  }

  override def addTrack(
      venueUid: Uid,
      coinCode: CoinCode,
      trackIdentifier: TrackIdentifier): Future[Unit] = {
    val eventualVenue = venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
    eventualVenue.flatMap { venue ⇒
      (queueCommandRouter ? RouteCommand(AddTrack(venue, coinCode, trackIdentifier))).mapTo[Unit]
    }
  }
}
