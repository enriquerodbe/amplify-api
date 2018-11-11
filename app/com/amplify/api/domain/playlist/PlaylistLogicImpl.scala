package com.amplify.api.domain.playlist

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Access, Token, Uid}
import com.amplify.api.domain.queue.Command.SetCurrentPlaylist
import com.amplify.api.domain.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.domain.venue.auth.VenueAuthService
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.FutureUtils._
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaylistLogicImpl @Inject()(
    playlistService: PlaylistService,
    venueService: VenueService,
    venueAuthService: VenueAuthService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends PlaylistLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]] = {
    withRefreshToken(venue)(playlistService.retrievePlaylists(venue))
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
      playlist ← withRefreshToken(venue)(playlistService.retrievePlaylist(playlistIdentifier))
      command = SetCurrentPlaylist(venue, playlist)
      result ← (queueCommandRouter ? RouteCommand(command)).mapTo[Unit]
    }
    yield result
  }

  private def withRefreshToken[T](venue: Venue)(f: Token[Access] ⇒ Future[T]): Future[T] = {
    venueAuthService.withRefreshToken(venue)(f)
  }
}
