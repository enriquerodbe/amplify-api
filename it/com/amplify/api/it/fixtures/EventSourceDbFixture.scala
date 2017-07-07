package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.EventSourcesTable
import com.amplify.api.domain.models.Venue

trait EventSourceDbFixture extends BaseDbFixture with EventSourcesTable {

  import profile.api._

  def findEventSources(venueId: Id[Venue]): Seq[EventSourceDb] = {
    eventSourcesTable.filter(_.venueId === venueId).result.await()
  }
}
