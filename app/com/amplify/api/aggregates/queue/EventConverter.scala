package com.amplify.api.aggregates.queue

import java.time.Instant

object EventConverter {

  def queueEventToQueueEventDb(
      queueCommand: CommandDb,
      queueEvent: Event): EventDb = {
    EventDb(
      queueCommandId = queueCommand.id,
      queueEventType = queueEvent.eventType,
      contentIdentifier = queueEvent.contentIdentifier,
      createdAt = Instant.now())
  }
}
