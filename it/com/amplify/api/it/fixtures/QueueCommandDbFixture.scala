package com.amplify.api.it.fixtures

import com.amplify.api.aggregates.queue.daos.{CommandDb, CommandsTable}
import com.amplify.api.domain.models.primitives.Id

trait QueueCommandDbFixture extends BaseDbFixture with CommandsTable {

  import profile.api._

  def findQueueCommands(venueId: Id): Seq[CommandDb] = {
    queueCommandsTable.filter(_.venueId === venueId).result.await()
  }
}
