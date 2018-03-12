package com.amplify.api.aggregates.queue.daos

import com.amplify.api.aggregates.queue.daos.CommonReads._
import com.amplify.api.aggregates.queue.daos.EventDbData._
import com.amplify.api.aggregates.queue.daos.EventType.EventType
import com.amplify.api.domain.models.{AuthProviderIdentifier, ContentProviderIdentifier}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object EventDbDataSerializer {

  private val eventTypeField = "event_type"

  private def commonWrites(eventType: EventType) = {
    Json.obj("version" → 1, eventTypeField → eventType)
  }

  private def currentPlaylistSetWrites(playlistIdentifier: ContentProviderIdentifier) = {
    commonWrites(EventType.CurrentPlaylistSet) ++
      Json.obj(playlistIdentifierField → playlistIdentifier.toString)
  }

  private val venueTracksRemovedWrites = commonWrites(EventType.VenueTracksRemoved)

  private def venueTrackAddedWrites(trackIdentifier: ContentProviderIdentifier) = {
    commonWrites(EventType.VenueTrackAdded) ++
      Json.obj(trackIdentifierField → trackIdentifier.toString)
  }

  private val trackFinishedWrites = commonWrites(EventType.TrackFinished)

  private def userTrackAddedWrites(
      userIdentifier: AuthProviderIdentifier,
      trackIdentifier: ContentProviderIdentifier) = {
    commonWrites(EventType.UserTrackAdded) ++ Json.obj(
      userIdentifierField → userIdentifier.toString,
      trackIdentifierField → trackIdentifier.toString)
  }

  private val currentTrackSkippedWrites = commonWrites(EventType.TrackSkipped)

  def serialize(data: EventDbData): JsValue = data match {
    case CurrentPlaylistSet(playlistIdentifier) ⇒ currentPlaylistSetWrites(playlistIdentifier)
    case VenueTracksRemoved ⇒ venueTracksRemovedWrites
    case VenueTrackAdded(trackIdentifier) ⇒ venueTrackAddedWrites(trackIdentifier)
    case TrackFinished ⇒ trackFinishedWrites
    case UserTrackAdded(userIdentifier, trackIdentifier) ⇒
      userTrackAddedWrites(userIdentifier, trackIdentifier)
    case TrackSkipped ⇒ currentTrackSkippedWrites
  }

  private val currentPlaylistSetReads = playlistIdentifierReads.map(CurrentPlaylistSet)

  private val venueTracksRemovedReads = Reads(_ ⇒ JsSuccess(VenueTracksRemoved))

  private val venueTrackAddedReads = trackIdentifierReads.map(VenueTrackAdded)

  private val trackFinishedReads = Reads(_ ⇒ JsSuccess(TrackFinished))

  private val userTrackAddedReads =
    (userIdentifierReads and trackIdentifierReads)(UserTrackAdded.apply _)

  private val trackSkippedReads = Reads(_ ⇒ JsSuccess(TrackSkipped))

  def deserialize(value: JsValue): EventDbData = {
    val reads = eventTypeReadsForValue(value)
    reads.reads(value) match {
      case JsSuccess(eventDbData, _) ⇒ eventDbData
      case JsError(errors) ⇒
        val msgs = errors.map { case (path, pathErrors) ⇒ s"$path: ${pathErrors.mkString(", ")}" }
        throw new IllegalArgumentException(msgs.mkString("; "))
    }
  }

  private implicit val eventTypeReads = Reads {
    case JsString(string) ⇒ JsSuccess(EventType.withName(string))
    case other ⇒ JsError(s"Expected JsString for EventType, got: $other")
  }

  private def eventTypeReadsForValue(value: JsValue) = {
    (value \ eventTypeField).as[EventType] match {
      case EventType.CurrentPlaylistSet ⇒ currentPlaylistSetReads
      case EventType.VenueTracksRemoved ⇒ venueTracksRemovedReads
      case EventType.VenueTrackAdded ⇒ venueTrackAddedReads
      case EventType.TrackFinished ⇒ trackFinishedReads
      case EventType.UserTrackAdded ⇒ userTrackAddedReads
      case EventType.TrackSkipped ⇒ trackSkippedReads
    }
  }
}
