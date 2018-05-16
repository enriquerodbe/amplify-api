package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.Event.PlaybackStarted
import com.amplify.api.services.VenueService
import javax.inject.Inject

class PlaybackNotifier @Inject()(venueService: VenueService) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(venue, PlaybackStarted, queue) ⇒
      venueService.startPlayback(venue, queue.futureItems.map(_.track.identifier))
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}
