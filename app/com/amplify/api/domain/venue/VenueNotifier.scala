package com.amplify.api.domain.venue

import akka.actor.{Actor, ActorRef, Props}
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.queue.QueueUpdated
import com.amplify.api.shared.controllers.dtos.QueueDtos.queueToQueueResponse

private class VenueNotifier(venue: Venue, webSocketListener: ActorRef) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(event, queue) if event.venueUid == venue.uid ⇒
      webSocketListener ! queueToQueueResponse(queue).toJson.toString()
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}

private object VenueNotifier {

  def props(venue: Venue, webSocketListener: ActorRef): Props = {
    Props(new VenueNotifier(venue, webSocketListener))
  }
}
