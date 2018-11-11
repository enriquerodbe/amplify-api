package com.amplify.api.domain.queue.serialization

import akka.serialization.SerializerWithStringManifest
import com.amplify.api.domain.queue.Event

class QueueEventProtobufSerializer extends SerializerWithStringManifest {

  override val identifier: Int = 111111

  private val CurrentPlaylistSetManifest = classOf[Event.CurrentPlaylistSet].getName
  private val PlaybackStartedManifest = Event.PlaybackStarted.getClass.getName
  private val VenueTracksRemovedManifest = Event.VenueTracksRemoved.getClass.getName
  private val VenueTrackAddedManifest = classOf[Event.VenueTrackAdded].getName
  private val TrackFinishedManifest = Event.TrackFinished.getClass.getName
  private val UserTrackAddedManifest = classOf[Event.UserTrackAdded].getName
  private val CurrentTrackSkippedManifest = Event.CurrentTrackSkipped.getClass.getName

  override def manifest(o: AnyRef): String = o match {
    case e: Event ⇒ e match {
      case _: Event.CurrentPlaylistSet ⇒ CurrentPlaylistSetManifest
      case Event.PlaybackStarted ⇒ PlaybackStartedManifest
      case Event.VenueTracksRemoved ⇒ VenueTracksRemovedManifest
      case _: Event.VenueTrackAdded ⇒ VenueTrackAddedManifest
      case Event.TrackFinished ⇒ TrackFinishedManifest
      case _: Event.UserTrackAdded ⇒ UserTrackAddedManifest
      case Event.CurrentTrackSkipped ⇒ CurrentTrackSkippedManifest
    }

    case _ ⇒ throw new IllegalStateException(s"No manifest for object $o")
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case evt: Event ⇒ ProtobufConverter.toProtobuf(evt).toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    val parser = manifest match {
      case CurrentPlaylistSetManifest ⇒ PbCurrentPlaylistSet
      case PlaybackStartedManifest ⇒ PbPlaybackStarted
      case VenueTracksRemovedManifest ⇒ PbVenueTracksRemoved
      case VenueTrackAddedManifest ⇒ PbVenueTrackAdded
      case TrackFinishedManifest ⇒ PbTrackFinished
      case UserTrackAddedManifest ⇒ PbUserTrackAdded
      case CurrentTrackSkippedManifest ⇒ PbCurrentTrackSkipped
    }
    ProtobufConverter.fromProtobuf(parser.parseFrom(bytes))
  }
}
