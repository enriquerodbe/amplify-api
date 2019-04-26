package com.amplify.api.domain.queue

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.queue.Command._
import com.amplify.api.domain.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.shared.configuration.EnvConfig
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

private class QueueServiceImpl @Inject()(
    envConfig: EnvConfig,
    @Named("queue-command-router") queueCommandRouter: ActorRef)(
    implicit ec: ExecutionContext)
  extends QueueService {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrieveAllowedPlaylist(venueUid: Uid): Future[Option[Playlist]] = {
    (queueCommandRouter ? RetrieveQueue(venueUid)).mapTo[Queue].map(_.allowedPlaylist)
  }

  override def setAllowedPlaylist(
      venueUid: Uid,
      playlistIdentifier: PlaylistIdentifier): Future[Unit] = {
    val command = SetAllowedPlaylist(venueUid, playlistIdentifier)
    (queueCommandRouter ? RouteCommand(command)).mapTo[Unit]
  }

  override def retrieveQueue(venueUid: Uid): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venueUid)).mapTo[Queue]
  }

  override def start(venueUid: Uid): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(StartPlayback(venueUid))).mapTo[Unit]
  }

  override def skip(venueUid: Uid): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(SkipCurrentTrack(venueUid))).mapTo[Unit]
  }

  override def finish(venueUid: Uid): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(FinishCurrentTrack(venueUid))).mapTo[Unit]
  }

  override def addPlaylistTracks(
      venueUid: Uid,
      playlistIdentifier: PlaylistIdentifier): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(AddPlaylistTracks(venueUid, playlistIdentifier))).mapTo[Unit]
  }

  override def addVenueTrack(venueUid: Uid, trackIdentifier: TrackIdentifier): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(AddVenueTrack(venueUid, trackIdentifier))).mapTo[Unit]
  }

  override def addTrack(
      venueUid: Uid,
      code: Code,
      trackIdentifier: TrackIdentifier): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(AddTrack(venueUid, code, trackIdentifier))).mapTo[Unit]
  }
}
