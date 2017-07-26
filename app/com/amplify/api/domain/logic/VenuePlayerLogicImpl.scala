package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.{PausePlayback, SkipCurrentTrack, StartPlayback}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.AuthenticatedVenueReq
import javax.inject.{Inject, Named}
import scala.concurrent.Future

class VenuePlayerLogicImpl @Inject()(
    @Named("queue-command-router") queueCommandRouter: ActorRef,
    envConfig: EnvConfig) extends VenuePlayerLogic {

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def play(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? StartPlayback(venue)).mapTo[Unit]
  }

  override def pause(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? PausePlayback(venue)).mapTo[Unit]
  }

  override def skip(venue: AuthenticatedVenueReq): Future[Unit] = {
    (queueCommandRouter ? SkipCurrentTrack(venue)).mapTo[Unit]
  }
}
