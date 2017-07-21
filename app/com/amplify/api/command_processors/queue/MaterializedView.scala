package com.amplify.api.command_processors.queue

import akka.actor.Actor
import com.amplify.api.command_processors.queue.MaterializedView.Materialize
import com.amplify.api.domain.models.Queue

class MaterializedView extends Actor {

  private var queue: Queue = Queue()

  override def receive: Receive = {
    case events: Seq[Event] ⇒ queue = events.foldLeft(queue)((queue, event) ⇒ event.process(queue))
    case Materialize ⇒ sender() ! queue
  }
}

object MaterializedView {

  case object Materialize
}
