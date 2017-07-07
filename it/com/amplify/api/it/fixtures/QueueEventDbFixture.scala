package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.QueueEventDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.QueueEventsTable
import com.amplify.api.domain.models.EventSource

trait QueueEventDbFixture extends BaseDbFixture with QueueEventsTable {

  import profile.api._

  def findQueueEvents(eventSourceId: Id[EventSource]): Seq[QueueEventDb] = {
    queueEventsTable.filter(_.eventSourceId === eventSourceId).result.await()
  }
}
