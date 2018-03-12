package com.amplify.api.aggregates.queue.daos

import com.amplify.api.aggregates.queue.daos.CommandDbData._
import com.amplify.api.aggregates.queue.daos.CommandType.CommandType
import com.amplify.api.aggregates.queue.daos.CommonReads._
import com.amplify.api.domain.models.primitives.PrimitivesSerializer._
import com.amplify.api.domain.models.primitives.Uid
import play.api.libs.functional.syntax._
import play.api.libs.json._

object CommandDbDataSerializer {

  private val venueUidField = "venue_uid"
  private val commandTypeField = "command_type"

  private def commonWrites(o: CommandDbData, commandType: CommandType) = {
    Json.obj("version" → 1, commandTypeField → commandType, venueUidField → o.venueUid.value)
  }

  private def setCurrentPlaylistWrites(o: SetCurrentPlaylist) = {
    commonWrites(o, CommandType.SetCurrentPlaylist) ++
      Json.obj(playlistIdentifierField → o.playlistIdentifier.toString)
  }

  private val startPlaybackWrites = commonWrites(_: CommandDbData, CommandType.StartPlayback)

  private val pausePlaybackWrites = commonWrites(_: CommandDbData, CommandType.PausePlayback)

  private val skipCurrentTrackWrites = commonWrites(_: CommandDbData, CommandType.SkipCurrentTrack)

  private def addTrackWrites(o: AddTrack) = {
    commonWrites(o, CommandType.AddTrack) ++ Json.obj(
      userIdentifierField → o.userIdentifier.toString,
      trackIdentifierField → o.trackIdentifier.toString)
  }

  def serialize(data: CommandDbData): JsValue = data match {
    case s: SetCurrentPlaylist ⇒ setCurrentPlaylistWrites(s)
    case s: StartPlayback ⇒ startPlaybackWrites(s)
    case p: PausePlayback ⇒ pausePlaybackWrites(p)
    case s: SkipCurrentTrack ⇒ skipCurrentTrackWrites(s)
    case a: AddTrack ⇒ addTrackWrites(a)
  }


  private val commonReads = (__ \ venueUidField).read[Uid]

  private val setCurrentPlaylistReads =
    (commonReads and playlistIdentifierReads)(SetCurrentPlaylist.apply _)

  private val startPlaybackReads = commonReads.map(StartPlayback)

  private val pausePlaybackReads = commonReads.map(PausePlayback)

  private val skipCurrentTrackReads = commonReads.map(SkipCurrentTrack)

  private val addTrackReads =
    (commonReads and userIdentifierReads and trackIdentifierReads)(AddTrack.apply _)

  def deserialize(value: JsValue): CommandDbData = {
    val reads = commandTypeReadsForValue(value)
    reads.reads(value) match {
      case JsSuccess(commandDbData, _) ⇒ commandDbData
      case JsError(errors) ⇒
        val msgs = errors.map { case (path, pathErrors) ⇒ s"$path: ${pathErrors.mkString(", ")}" }
        throw new IllegalArgumentException(msgs.mkString("; "))
    }
  }

  private implicit val commandTypeReads = Reads {
    case JsString(string) ⇒ JsSuccess(CommandType.withName(string))
    case other ⇒ JsError(s"Expected JsString for CommandType, got: $other")
  }

  private def commandTypeReadsForValue(value: JsValue) = {
    (value \ commandTypeField).as[CommandType] match {
      case CommandType.SetCurrentPlaylist ⇒ setCurrentPlaylistReads
      case CommandType.StartPlayback ⇒ startPlaybackReads
      case CommandType.PausePlayback ⇒ pausePlaybackReads
      case CommandType.SkipCurrentTrack ⇒ skipCurrentTrackReads
      case CommandType.AddTrack ⇒ addTrackReads
    }
  }
}
