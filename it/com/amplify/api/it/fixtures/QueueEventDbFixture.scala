package com.amplify.api.it.fixtures

import com.amplify.api.command_processors.queue.CommandProcessor.Command
import com.amplify.api.command_processors.queue.{EventDb, EventsTable}
import com.amplify.api.daos.primitives.Id

trait QueueEventDbFixture extends BaseDbFixture with EventsTable {

  import profile.api._

  def findQueueEvents(queueCommandId: Id[Command]): Seq[EventDb] = {
    queueEventsTable.filter(_.queueCommandId === queueCommandId).result.await()
  }
}
