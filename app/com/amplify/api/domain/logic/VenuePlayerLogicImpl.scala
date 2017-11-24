package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.{AddTrack, PausePlayback, SkipCurrentTrack, StartPlayback}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.{AuthenticatedVenueReq, ContentProviderIdentifier, User}
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.services.VenueService
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends VenuePlayerLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def play(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? StartPlayback(venue.unauthenticated)).mapTo[Unit]
  }

  override def pause(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? PausePlayback(venue.unauthenticated)).mapTo[Unit]
  }

  override def skip(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? SkipCurrentTrack(venue.unauthenticated)).mapTo[Unit]
  }

  override def addTrack(
      uid: Uid,
      user: User,
      trackIdentifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      venue ← venueService.retrieve(uid)
      result ← (queueCommandRouter ? AddTrack(venue, user, trackIdentifier)).mapTo[Unit]
    }
    yield result
  }
}
