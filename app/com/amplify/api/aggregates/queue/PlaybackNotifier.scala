package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.Event.PlaybackStarted
import com.amplify.api.services.{VenueAuthService, VenueService}
import javax.inject.Inject

class PlaybackNotifier @Inject()(
    venueService: VenueService,
    venueAuthService: VenueAuthService) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(venue, PlaybackStarted, queue) ⇒
      venueAuthService.withRefreshToken(venue) {
        venueService.startPlayback(queue.futureItems.map(_.track.identifier))
      }
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}
