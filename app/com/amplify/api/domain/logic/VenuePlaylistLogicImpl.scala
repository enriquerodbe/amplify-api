package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.Command.SetCurrentPlaylist
import com.amplify.api.aggregates.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.exceptions.VenueNotFoundByUid
import com.amplify.api.services.VenueService
import com.amplify.api.utils.FutureUtils._
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VenuePlaylistLogicImpl @Inject()(
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends VenuePlaylistLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]] = {
    venueService.retrievePlaylists(venue)
  }

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
    for {
      playlist ← venueService.retrievePlaylist(venue, playlistIdentifier)
      command = SetCurrentPlaylist(venue, playlist)
      result ← (queueCommandRouter ? RouteCommand(command)).mapTo[Unit]
    }
    yield result
  }
}