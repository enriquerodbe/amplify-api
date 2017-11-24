package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.CommandProcessor.Command
import com.amplify.api.aggregates.queue.daos.CommandDb
import java.time.Instant

object CommandConverter {

  def commandToCommandDb(command: Command): CommandDb = {
    CommandDb(
      venueId = command.venue.id,
      userId = command.userId,
      queueCommandType = command.queueCommandType,
      contentIdentifier = command.contentIdentifier,
      createdAt = Instant.now())
  }
}
