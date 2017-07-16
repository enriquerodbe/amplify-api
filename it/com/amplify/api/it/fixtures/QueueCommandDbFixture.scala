package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.QueueCommandDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.QueueCommandsTable
import com.amplify.api.domain.models.Venue

trait QueueCommandDbFixture extends BaseDbFixture with QueueCommandsTable {

  import profile.api._

  def findQueueCommands(venueId: Id[Venue]): Seq[QueueCommandDb] = {
    queueCommandsTable.filter(_.venueId === venueId).result.await()
  }
}
