package com.amplify.api.services.converters

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.domain.models.EventSource.{PausePlayback, SetCurrentPlaylist, StartPlayback}
import com.amplify.api.domain.models._
import java.time.Instant

object EventSourceConverter {

  def eventSourceToEventSourceDb(eventSource: EventSource): EventSourceDb = eventSource match {
    case SetCurrentPlaylist(venue, identifier) ⇒ setCurrentPlaylistEventSourceDb(venue, identifier)
    case StartPlayback(venue) ⇒ startPlaybackEventSourceDb(venue)
    case PausePlayback(venue) ⇒ pausePlaybackEventSourceDb(venue)
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

  private def startPlaybackEventSourceDb(venue: AuthenticatedVenue) = {
    EventSourceDb(
      venueId = venue.id,
      userId = None,
      eventType = EventSourceType.StartPlayback,
      contentIdentifier = None,
      createdAt = Instant.now())
  }

  private def pausePlaybackEventSourceDb(venue: AuthenticatedVenue) = {
    EventSourceDb(
      venueId = venue.id,
      userId = None,
      eventType = EventSourceType.PausePlayback,
      contentIdentifier = None,
      createdAt = Instant.now())
  }
}
