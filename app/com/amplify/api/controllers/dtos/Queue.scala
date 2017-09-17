package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Track.{CurrentTrackResponse, QueueTrackResponse, trackToCurrentTrackResponse, trackToQueueTrackResponse}
import com.amplify.api.domain.models.{Queue ⇒ ModelQueue}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Queue {

  case class QueueResponse(
      currentPlaylist: Option[String],
      currentTrack: Option[CurrentTrackResponse],
      tracks: Seq[QueueTrackResponse])
  def queueToQueueResponse(queue: ModelQueue): QueueResponse = {
    QueueResponse(
      queue.currentPlaylist.map(_.info.identifier.toString),
      queue.currentItem.map(item ⇒ {
        val currentTrackIndex = queue.allItems.indexOf(item)
        trackToCurrentTrackResponse(item.track, currentTrackIndex)
      }),
      queue.allItems.map(item ⇒ trackToQueueTrackResponse(item.track)))
  }
  implicit val queueResponseWrites: Writes[QueueResponse] = {
    JsonNaming.snakecase(Json.writes[QueueResponse])
  }
}
