package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.EventType.QueueEventType
import com.amplify.api.domain.models._

sealed trait Event {

  def eventType: QueueEventType

  def contentIdentifier: Option[ContentProviderIdentifier]
}

object Event {

  case class CurrentPlaylistSet(playlist: Playlist) extends Event {

    override def eventType: QueueEventType = EventType.CurrentPlaylistSet

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.info.identifier)
    }
  }

  case object VenueTracksRemoved extends Event {

    override def eventType: QueueEventType = EventType.VenueTracksRemoved

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class VenueTrackAdded(track: Track) extends Event {

    override def eventType: QueueEventType = EventType.VenueTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(track.identifier)
    }
  }

  case object TrackFinished extends Event {

    override def eventType: QueueEventType = EventType.TrackFinished

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class UserTrackAdded(user: User, identifier: ContentProviderIdentifier) extends Event {

    override def eventType: QueueEventType = EventType.UserTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(identifier)
  }

  case object CurrentTrackSkipped extends Event {

    override def eventType: QueueEventType = EventType.CurrentTrackSkipped

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }
}
