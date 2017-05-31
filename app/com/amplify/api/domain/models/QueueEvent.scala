package com.amplify.api.domain.models

import com.amplify.api.domain.models.QueueEventType.QueueEventType

sealed trait QueueEvent {

  def eventType: QueueEventType

  def contentIdentifier: Option[ContentProviderIdentifier]

  def process(queue: Queue): Queue
}

object QueueEvent {

  case object RemoveVenueTracks extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.RemoveVenueTracks

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = queue.removeVenueTracks()
  }

  case class AddVenueTrack(track: Track) extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.AddVenueTrack

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(track.contentProviderIdentifier)
    }

    override def process(queue: Queue): Queue = queue.addVenueTrack(track)
  }

  case object RemoveAllTracks extends QueueEvent {

    override def eventType: QueueEventType = QueueEventType.RemoveAllTracks

    override def contentIdentifier: Option[ContentProviderIdentifier] = None

    override def process(queue: Queue): Queue = Queue()
  }
}
