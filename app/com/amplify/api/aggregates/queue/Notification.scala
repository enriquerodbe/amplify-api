package com.amplify.api.aggregates.queue

import com.amplify.api.domain.models.{Queue, Venue}

sealed trait Notification {

  def venue: Venue
}

case class QueueUpdated(venue: Venue, event: Event, queue: Queue) extends Notification
