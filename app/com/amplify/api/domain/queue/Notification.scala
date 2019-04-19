package com.amplify.api.domain.queue

import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.Uid

sealed trait Notification {

  def venueUid: Uid
}

case class QueueUpdated(event: QueueEvent, queue: Queue) extends Notification {
  override def venueUid: Uid = event.venueUid
}
