package com.amplify.api.aggregates.queue

import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.Uid

sealed trait Notification {

  def venueUid: Uid
}

case class QueueUpdated(venueUid: Uid, queue: Queue) extends Notification
