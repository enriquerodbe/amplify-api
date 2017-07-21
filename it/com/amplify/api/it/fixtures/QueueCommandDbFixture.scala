package com.amplify.api.it.fixtures

import com.amplify.api.command_processors.queue.{CommandDb, CommandsTable}
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.Venue

trait QueueCommandDbFixture extends BaseDbFixture with CommandsTable {

  import profile.api._

  def findQueueCommands(venueId: Id[Venue]): Seq[CommandDb] = {
    queueCommandsTable.filter(_.venueId === venueId).result.await()
  }
}
