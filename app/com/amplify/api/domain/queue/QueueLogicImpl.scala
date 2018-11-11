package com.amplify.api.domain.queue

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{CoinToken, Queue, TrackIdentifier, Venue}
import com.amplify.api.domain.queue.Command.{AddTrack, FinishCurrentTrack, SkipCurrentTrack, StartPlayback}
import com.amplify.api.domain.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.FutureUtils._
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class QueueLogicImpl @Inject()(
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext) extends QueueLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def retrieveQueue(venue: Venue): Future[Queue] = {
    (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
  }

  override def skip(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(SkipCurrentTrack(venue))).mapTo[Unit]
  }

  override def start(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(StartPlayback(venue))).mapTo[Unit]
  }

  override def finish(venue: Venue): Future[Unit] = {
    (queueCommandRouter ? RouteCommand(FinishCurrentTrack(venue))).mapTo[Unit]
  }

  override def addTrack(
      venueUid: Uid,
      coinToken: CoinToken,
      trackIdentifier: TrackIdentifier): Future[Unit] = {
    val eventualVenue = venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
    eventualVenue.flatMap { venue ⇒
      (queueCommandRouter ? RouteCommand(AddTrack(venue, coinToken, trackIdentifier))).mapTo[Unit]
    }
  }
}
