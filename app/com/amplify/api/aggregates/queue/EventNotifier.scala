package com.amplify.api.aggregates.queue

import akka.actor.{Actor, ActorRef, Props}
import com.amplify.api.controllers.dtos.Queue.queueToQueueResponse
import com.amplify.api.domain.models.primitives.Uid
import play.api.libs.json.Json

class EventNotifier(venueUid: Uid, webSocketListener: ActorRef) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(uid, queue) if uid == venueUid ⇒
      webSocketListener ! Json.toJson(queueToQueueResponse(queue)).toString()
  }

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[QueueUpdated])
  }
}

object EventNotifier {

  def props(venueUid: Uid, webSocketListener: ActorRef): Props = {
    Props(new EventNotifier(venueUid, webSocketListener))
  }
}