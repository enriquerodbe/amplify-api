package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.Event.PlaybackStarted
import com.amplify.api.domain.models.Venue
import com.amplify.api.services.VenueService
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject

class PlaybackNotifier @Inject()(@Assisted venue: Venue, venueService: VenueService) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(affectedVenue, PlaybackStarted, queue) if affectedVenue.uid == venue.uid ⇒
      venueService.startPlayback(venue, queue.futureItems.map(_.track.identifier))
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}

object PlaybackNotifier {

  trait Factory {
    def apply(venue: Venue): Actor
  }
}
