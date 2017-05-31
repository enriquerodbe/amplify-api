package com.amplify.api.services.converters

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.domain.models._
import java.time.Instant

object EventSourceConverter {

  def eventSourceToEventSourceDb(eventSource: EventSource): EventSourceDb = {
    EventSourceDb(
      venueId = eventSource.venue.id,
      userId = None,
      eventType = EventSourceType.SetCurrentPlaylist,
      contentIdentifier = eventSource.contentIdentifier,
      createdAt = Instant.now())
  }
}
