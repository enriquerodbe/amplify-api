package com.amplify.api.services.converters

import com.amplify.api.daos.models.{EventSourceDb, QueueEventDb}
import com.amplify.api.domain.models.QueueEvent.{AddVenueTrack, RemoveVenueTracks, StartPlaying}
import com.amplify.api.domain.models._
import java.time.Instant

object QueueEventConverter {

  def queueEventToQueueEventDb(eventSource: EventSourceDb, queueEvent: QueueEvent): QueueEventDb = {
    queueEvent match {
      case RemoveVenueTracks ⇒ removeVenueTracksQueueEventDb(eventSource)
      case AddVenueTrack(track) ⇒ addVenueTrackQueueEventDb(eventSource, track)
      case StartPlaying ⇒ startPlayingQueueEventDb(eventSource)
    }
  }

  private def removeVenueTracksQueueEventDb(eventSource: EventSourceDb) = {
    QueueEventDb(
      eventSourceId = eventSource.id,
      eventType = QueueEventType.RemoveVenueTracks,
      contentIdentifier = None,
      createdAt = Instant.now()
    )
  }

  private def addVenueTrackQueueEventDb(eventSource: EventSourceDb, track: Track): QueueEventDb = {
    QueueEventDb(
      eventSourceId = eventSource.id,
      eventType = QueueEventType.AddVenueTrack,
      contentIdentifier = Some(track.contentProviderIdentifier),
      createdAt = Instant.now()
    )
  }

  private def startPlayingQueueEventDb(eventSource: EventSourceDb): QueueEventDb = {
    QueueEventDb(
      eventSourceId = eventSource.id,
      eventType = QueueEventType.StartPlaying,
      contentIdentifier = None,
      createdAt = Instant.now()
    )
  }
}
