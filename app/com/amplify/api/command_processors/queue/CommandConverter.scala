package com.amplify.api.command_processors.queue

import com.amplify.api.command_processors.queue.CommandProcessor.Command
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
