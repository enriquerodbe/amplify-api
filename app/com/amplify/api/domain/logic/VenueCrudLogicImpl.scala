package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.Command.SetCurrentPlaylist
import com.amplify.api.aggregates.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.services.VenueService
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VenueCrudLogicImpl @Inject()(
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends VenueCrudLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrievePlaylists(venue: VenueReq): Future[Seq[PlaylistInfo]] = {
    venueService.retrievePlaylists(venue)
  }

  override def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]] = {
    for {
      venue ← venueService.retrieve(uid)
      queue ← retrieveQueue(venue)
    }
    yield queue.currentPlaylist
  }

  override def setCurrentPlaylist(
      venueReq: VenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      playlist ← venueService.retrievePlaylist(venueReq, playlistIdentifier)
      command = SetCurrentPlaylist(venueReq.venue, playlist)
      result ← (queueCommandRouter ? RouteCommand(command)).mapTo[Unit]
    }
    yield result
  }

  override def retrieveQueue(venue: Venue): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
  }

  override def retrieveAll(): Future[Seq[Venue]] = venueService.retrieveAll()
}
