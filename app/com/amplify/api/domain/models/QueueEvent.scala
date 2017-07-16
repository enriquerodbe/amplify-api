package com.amplify.api.domain.models

import com.amplify.api.domain.models.QueueEventType.QueueEventType
import scala.util.Try

sealed trait QueueEvent {

  def eventType: QueueEventType

  def contentIdentifier: Option[ContentProviderIdentifier]

  def process(queue: Queue): Try[Queue]
}

object QueueEvent {

  case class CurrentPlaylistSet(playlist: Playlist) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.CurrentPlaylistSet

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.identifier.identifier)
    }

    override def process(queue: Queue): Try[Queue] = queue.setCurrentPlaylist(playlist)
  }

  case object VenueTracksRemoved extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.VenueTracksRemoved

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.removeVenueTracks()
  }

  case class VenueTrackAdded(track: Track) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.VenueTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(track.identifier)
    }

    override def process(queue: Queue): Try[Queue] = queue.addVenueTrack(track)
  }

  case object AllTracksRemoved extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.AllTracksRemoved

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.removeAllTracks()
  }

  case object TrackFinished extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.TrackFinished

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.trackFinished()
  }

  case class UserTrackAdded(user: User, identifier: ContentProviderIdentifier) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.UserTrackAdded

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(identifier)

    override def process(queue: Queue): Try[Queue] = queue.addUserTrack(user, identifier)
  }

  case object CurrentTrackSkipped extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.CurrentTrackSkipped

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.skipCurrentTrack()
  }
}
