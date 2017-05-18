package com.amplify.api.services.converters

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.domain.models.EventSource.{SetCurrentPlaylist, StartPlaying}
import com.amplify.api.domain.models._
import java.time.Instant

object EventSourceConverter {

  def eventSourceToEventSourceDb(eventSource: EventSource): EventSourceDb = eventSource match {
    case SetCurrentPlaylist(venue, identifier) ⇒ setCurrentPlaylistEventSourceDb(venue, identifier)
    case StartPlaying(venue) ⇒ startPlayingEventSourceDb(venue)
  }

  private def setCurrentPlaylistEventSourceDb(
      venue: AuthenticatedVenue,
      identifier: ContentProviderIdentifier) = {
    EventSourceDb(
      venueId = venue.id,
      userId = None,
      eventType = EventSourceType.SetCurrentPlaylist,
      contentIdentifier = Some(identifier),
      createdAt = Instant.now())
  }

  private def startPlayingEventSourceDb(venue: AuthenticatedVenue) = {
    EventSourceDb(
      venueId = venue.id,
      userId = None,
      eventType = EventSourceType.StartPlaying,
      contentIdentifier = None,
      createdAt = Instant.now())
  }
}
