package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.SetCurrentPlaylist
import com.amplify.api.aggregates.queue.CommandRouter.RetrieveQueue
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
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

  override def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]] = {
    venueService.retrievePlaylists(venue)
  }

  override def setCurrentPlaylist(
      venueReq: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    (queueCommandRouter ? SetCurrentPlaylist(venueReq, playlistIdentifier)).mapTo[Unit]
  }

  override def retrieveQueue(venue: AuthenticatedVenueReq): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
  }

  override def retrieveAll(): Future[Seq[Venue]] = venueService.retrieveAll()
}
