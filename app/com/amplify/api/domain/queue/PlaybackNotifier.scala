package com.amplify.api.domain.queue

import akka.actor.Actor
import com.amplify.api.domain.queue.Event.PlaybackStarted
import com.amplify.api.domain.venue.auth.VenueAuthService
import javax.inject.Inject

class PlaybackNotifier @Inject()(
    queueService: QueueService,
    venueAuthService: VenueAuthService) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(venue, PlaybackStarted, queue) ⇒
      venueAuthService.withRefreshToken(venue) {
        queueService.startPlayback(queue.futureItems.map(_.track.identifier))
      }
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}
