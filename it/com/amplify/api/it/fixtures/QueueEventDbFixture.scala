package com.amplify.api.it.fixtures

import com.amplify.api.aggregates.queue.{EventDb, EventsTable}
import com.amplify.api.domain.models.primitives.Id

trait QueueEventDbFixture extends BaseDbFixture with EventsTable {

  import profile.api._

  def findQueueEvents(queueCommandId: Id): Seq[EventDb] = {
    queueEventsTable.filter(_.queueCommandId === queueCommandId).result.await()
  }
}
