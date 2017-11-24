package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.SetCurrentPlaylist
import com.amplify.api.aggregates.queue.CommandRouter.{RetrieveCurrentPlaylist, RetrieveQueue}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Token
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

  override def retrieveCurrentPlaylist(uid: String): Future[Option[Playlist]] = {
    for {
      venue ← venueService.retrieve(uid)
      playlist ← (queueCommandRouter ? RetrieveCurrentPlaylist(venue)).mapTo[Option[Playlist]]
    }
    yield playlist
  }

  override def setCurrentPlaylist(
      venueReq: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      playlist ← venueService.retrievePlaylist(venueReq, playlistIdentifier)
      unauthenticatedVenue = venueReq.unauthenticated
      result ← (queueCommandRouter ? SetCurrentPlaylist(unauthenticatedVenue, playlist)).mapTo[Unit]
    }
    yield result
  }

  override def setFcmToken(venue: AuthenticatedVenue, token: Token): Future[Unit] = {
    venueService.setFcmToken(venue, token)
  }

  override def retrieveQueue(venue: AuthenticatedVenueReq): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
  }

  override def retrieveAll(): Future[Seq[Venue]] = venueService.retrieveAll()
}
