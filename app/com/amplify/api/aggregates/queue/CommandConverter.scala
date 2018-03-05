package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.daos.CommandDb
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

object CommandConverter {

  def commandToCommandDb(command: Command, venueId: Id, maybeUserId: Option[Id]): CommandDb = {
    CommandDb(
      venueId = venueId,
      userId = maybeUserId,
      queueCommandType = command.queueCommandType,
      contentIdentifier = command.contentIdentifier,
      createdAt = Instant.now())
  }
}
