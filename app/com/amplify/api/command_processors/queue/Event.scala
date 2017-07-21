package com.amplify.api.command_processors.queue

import com.amplify.api.command_processors.queue.EventType.QueueEventType
import com.amplify.api.domain.models._

sealed trait Event {

  def eventType: QueueEventType

  def contentIdentifier: Option[ContentProviderIdentifier]

  def process(queue: Queue): Queue
}

object Event {

  case class CurrentPlaylistSet(playlist: Playlist) extends Event {

    override def eventType: QueueEventType = EventType.CurrentPlaylistSet

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.info.identifier)
    }

    override def process(queue: Queue): Queue = queue.setCurrentPlaylist(playlist)
  }

  case object VenueTracksRemoved extends Event {

    override def eventType: QueueEventType = EventType.VenueTracksRemoved

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = queue.removeVenueTracks()
  }

  case class VenueTrackAdded(track: Track) extends Event {

    override def eventType: QueueEventType = EventType.VenueTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(track.identifier)
    }

    override def process(queue: Queue): Queue = queue.addVenueTrack(track)
  }

  case object AllTracksRemoved extends Event {

    override def eventType: QueueEventType = EventType.AllTracksRemoved

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = queue.removeAllTracks()
  }

  case object TrackFinished extends Event {

    override def eventType: QueueEventType = EventType.TrackFinished

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = queue.trackFinished()
  }

  case class UserTrackAdded(user: User, identifier: ContentProviderIdentifier) extends Event {

    override def eventType: QueueEventType = EventType.UserTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(identifier)

    override def process(queue: Queue): Queue = queue.addUserTrack(user, identifier)
  }

  case object CurrentTrackSkipped extends Event {

    override def eventType: QueueEventType = EventType.CurrentTrackSkipped

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = queue.skipCurrentTrack()
  }
}
