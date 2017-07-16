package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.QueueEventDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.QueueEventsTable
import com.amplify.api.domain.models.QueueCommand

trait QueueEventDbFixture extends BaseDbFixture with QueueEventsTable {

  import profile.api._

  def findQueueEvents(queueCommandId: Id[QueueCommand]): Seq[QueueEventDb] = {
    queueEventsTable.filter(_.queueCommandId === queueCommandId).result.await()
  }
}
