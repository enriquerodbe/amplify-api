package com.amplify.api.aggregates.queue.serialization

import akka.serialization.SerializerWithStringManifest
import com.amplify.api.aggregates.queue.Event

class QueueEventProtobufSerializer extends SerializerWithStringManifest {

  override val identifier: Int = 111111

  override def manifest(o: AnyRef): String = o.getClass.getName

  private val CurrentPlaylistSetManifest = classOf[Event.CurrentPlaylistSet].getName
  private val VenueTracksRemovedManifest = Event.VenueTracksRemoved.getClass.getName
  private val VenueTrackAddedManifest = classOf[Event.VenueTrackAdded].getName
  private val TrackFinishedManifest = Event.TrackFinished.getClass.getName
  private val UserTrackAddedManifest = classOf[Event.UserTrackAdded].getName
  private val CurrentTrackSkippedManifest = Event.CurrentTrackSkipped.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case evt: Event ⇒ ProtobufConverter.toProtobuf(evt).toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    val parser = manifest match {
      case CurrentPlaylistSetManifest ⇒ CurrentPlaylistSet
      case VenueTracksRemovedManifest ⇒ VenueTracksRemoved
      case VenueTrackAddedManifest ⇒ VenueTrackAdded
      case TrackFinishedManifest ⇒ TrackFinished
      case UserTrackAddedManifest ⇒ UserTrackAdded
      case CurrentTrackSkippedManifest ⇒ CurrentTrackSkipped
    }
    ProtobufConverter.fromProtobuf(parser.parseFrom(bytes))
  }
}
