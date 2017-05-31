package com.amplify.api.services.converters

import com.amplify.api.daos.models.{EventSourceDb, QueueEventDb}
import com.amplify.api.domain.models._
import java.time.Instant

object QueueEventConverter {

  def queueEventToQueueEventDb(eventSource: EventSourceDb, queueEvent: QueueEvent): QueueEventDb = {
    QueueEventDb(
      eventSourceId = eventSource.id,
      eventType = queueEvent.eventType,
      contentIdentifier = queueEvent.contentIdentifier,
      createdAt = Instant.now())
  }
}
