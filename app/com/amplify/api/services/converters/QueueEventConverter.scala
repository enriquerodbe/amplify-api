package com.amplify.api.services.converters

import com.amplify.api.daos.models.{QueueCommandDb, QueueEventDb}
import com.amplify.api.domain.models._
import java.time.Instant

object QueueEventConverter {

  def queueEventToQueueEventDb(
      queueCommand: QueueCommandDb,
      queueEvent: QueueEvent): QueueEventDb = {
    QueueEventDb(
      queueCommandId = queueCommand.id,
      eventType = queueEvent.eventType,
      contentIdentifier = queueEvent.contentIdentifier,
      createdAt = Instant.now())
  }
}
