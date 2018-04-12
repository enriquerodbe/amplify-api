package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.Command.{AddTrack, FinishCurrentTrack, SkipCurrentTrack}
import com.amplify.api.aggregates.queue.CommandRouter.RouteCommand
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{TrackIdentifier, User, Venue}
import com.amplify.api.services.VenueService
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class VenuePlayerLogicImpl @Inject()(
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends VenuePlayerLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def play(venue: Venue): Future[Unit] = {
    Future.successful(())
  }

  override def pause(venue: Venue): Future[Unit] = {
    Future.successful(())
  }

  override def skip(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(SkipCurrentTrack(venue))).mapTo[Unit]
  }

  override def finish(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(FinishCurrentTrack(venue))).mapTo[Unit]
  }

  override def addTrack(
      venueUid: Uid,
      user: User,
      trackIdentifier: TrackIdentifier): Future[Unit] = {
    val eventualVenue = venueService.retrieve(venueUid)
    eventualVenue.flatMap { venue ⇒
      (queueCommandRouter ? RouteCommand(AddTrack(venue, user, trackIdentifier))).mapTo[Unit]
    }
  }
}
