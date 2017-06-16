package com.amplify.api.domain.models

import com.amplify.api.domain.models.QueueEventType.QueueEventType
import scala.util.Try

sealed trait QueueEvent {

  def eventType: QueueEventType

  def contentIdentifier: Option[ContentProviderIdentifier]

  def process(queue: Queue): Try[Queue]
}

object QueueEvent {

  case class SetCurrentPlaylist(playlist: Playlist) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.identifier.identifier)
    }

    override def process(queue: Queue): Try[Queue] = queue.setCurrentPlaylist(playlist)
  }

  case object RemoveVenueTracks extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.RemoveVenueTracks

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.removeVenueTracks()
  }

  case class AddVenueTrack(track: Track) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.AddVenueTrack

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(track.contentProviderIdentifier)
    }

    override def process(queue: Queue): Try[Queue] = queue.addVenueTrack(track)
  }

  case object RemoveAllTracks extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.RemoveAllTracks

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.removeAllTracks()
  }

  case object TrackFinished extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.TrackFinished

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Try[Queue] = queue.trackFinished()
  }

  case class AddUserTrack(user: User, identifier: ContentProviderIdentifier) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.AddUserTrack

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(identifier)

    override def process(queue: Queue): Try[Queue] = queue.addUserTrack(user, identifier)
  }
}
