package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.models.{ContentIdentifier, PlaylistIdentifier, TrackIdentifier}
import com.amplify.api.domain.queue.QueueEventType.QueueEventType

sealed trait QueueEvent {

  def venueUid: Uid
  def eventType: QueueEventType
  def coinCode: Option[Code]
  def contentIdentifier: Option[ContentIdentifier]
}

case class AllowedPlaylistSet(
    override val venueUid: Uid,
    playlistIdentifier: PlaylistIdentifier) extends QueueEvent {

  override val eventType: QueueEventType = QueueEventType.CurrentPlaylistSet
  override val coinCode: Option[Code] = None
  override val contentIdentifier: Option[ContentIdentifier] = Some(playlistIdentifier)
}

case class PlaybackStarted(override val venueUid: Uid) extends QueueEvent {

  override val eventType: QueueEventType = QueueEventType.PlaybackStarted
  override val coinCode: Option[Code] = None
  override val contentIdentifier: Option[ContentIdentifier] = None
}

case class TrackFinished(override val venueUid: Uid) extends QueueEvent {

  override val eventType: QueueEventType = QueueEventType.TrackFinished
  override val coinCode: Option[Code] = None
  override val contentIdentifier: Option[ContentIdentifier] = None
}

case class PlaylistTracksAdded(
    override val venueUid: Uid,
    playlistIdentifier: PlaylistIdentifier) extends QueueEvent {

  override def eventType: QueueEventType = QueueEventType.PlaylistTracksAdded
  override def coinCode: Option[Code] = None
  override def contentIdentifier: Option[ContentIdentifier] = Some(playlistIdentifier)
}

case class VenueTrackAdded(
    override val venueUid: Uid,
    trackIdentifier: TrackIdentifier) extends QueueEvent {

  override val eventType: QueueEventType = QueueEventType.VenueTrackAdded
  override val coinCode: Option[Code] = None
  override val contentIdentifier: Option[ContentIdentifier] = Some(trackIdentifier)
}

case class UserTrackAdded(
    override val venueUid: Uid,
    code: Code,
    trackIdentifier: TrackIdentifier) extends QueueEvent {

  override val eventType: QueueEventType = QueueEventType.UserTrackAdded
  override val coinCode: Option[Code] = Some(code)
  override val contentIdentifier: Option[ContentIdentifier] = Some(trackIdentifier)
}

case class CurrentTrackSkipped(override val venueUid: Uid) extends QueueEvent {

  override def eventType: QueueEventType = QueueEventType.CurrentTrackSkipped
  override def coinCode: Option[Code] = None
  override def contentIdentifier: Option[ContentIdentifier] = None
}

object QueueEvent {

  def apply(
      venueUid: Uid,
      eventType: QueueEventType,
      code: Option[Code],
      contentIdentifier: Option[ContentIdentifier]): QueueEvent = eventType match {
    case QueueEventType.CurrentPlaylistSet ⇒
      AllowedPlaylistSet(venueUid, contentIdentifier.get.asInstanceOf[PlaylistIdentifier])
    case QueueEventType.PlaybackStarted ⇒ PlaybackStarted(venueUid)
    case QueueEventType.TrackFinished ⇒ TrackFinished(venueUid)
    case QueueEventType.UserTrackAdded ⇒
      UserTrackAdded(venueUid, code.get, contentIdentifier.get.asInstanceOf[TrackIdentifier])
    case QueueEventType.CurrentTrackSkipped ⇒ CurrentTrackSkipped(venueUid)
  }

  def toTuple(
      dbQueueEvent: QueueEvent
  ): Option[(Uid, QueueEventType, Option[Code], Option[ContentIdentifier])] = {
    Some(
      dbQueueEvent.venueUid,
      dbQueueEvent.eventType,
      dbQueueEvent.coinCode,
      dbQueueEvent.contentIdentifier
    )
  }
}

object QueueEventType extends Enumeration {
  type QueueEventType = Value

  val
  CurrentPlaylistSet,
  PlaybackStarted,
  CurrentTrackSkipped,
  TrackFinished,
  PlaylistTracksAdded,
  VenueTrackAdded,
  UserTrackAdded = Value
}
