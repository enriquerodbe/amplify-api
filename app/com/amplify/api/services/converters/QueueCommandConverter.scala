package com.amplify.api.services.converters

import com.amplify.api.daos.models.QueueCommandDb
import com.amplify.api.domain.models._
import java.time.Instant

object QueueCommandConverter {

  def queueCommandToQueueCommandDb(queueCommand: QueueCommand): QueueCommandDb = {
    QueueCommandDb(
      venueId = queueCommand.venue.id,
      userId = None,
      queueCommandType = queueCommand.queueCommandType,
      contentIdentifier = queueCommand.contentIdentifier,
      createdAt = Instant.now())
  }
}
