package com.amplify.api.aggregates.queue

import akka.actor.{Actor, ActorRef, Props}
import com.amplify.api.controllers.dtos.Queue.queueToQueueResponse
import com.amplify.api.domain.models.Venue

class VenueNotifier(venue: Venue, webSocketListener: ActorRef) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(affectedVenue, _, queue) if affectedVenue.uid == venue.uid ⇒
      webSocketListener ! queueToQueueResponse(queue).toJson.toString()
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}

object VenueNotifier {

  def props(venue: Venue, webSocketListener: ActorRef): Props = {
    Props(new VenueNotifier(venue, webSocketListener))
  }
}
