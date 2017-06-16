package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Track.{TrackResponse, trackToTrackResponse}
import com.amplify.api.domain.models.{Queue ⇒ ModelQueue}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Queue {

  case class QueueResponse(
      currentPlaylist: Option[String],
      currentTrack: Option[TrackResponse],
      tracks: Seq[TrackResponse])
  def queueToQueueResponse(queue: ModelQueue): QueueResponse = {
    QueueResponse(
      queue.currentPlaylist.map(_.identifier.identifier.toString),
      queue.currentTrack.map(trackToTrackResponse),
      queue.items.map(item ⇒ trackToTrackResponse(item.track)))
  }
  implicit val queueResponseWrites: Writes[QueueResponse] = {
    JsonNaming.snakecase(Json.writes[QueueResponse])
  }
}
