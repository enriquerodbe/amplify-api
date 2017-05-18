package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Track.{TrackResponse, trackToTrackResponse}
import com.amplify.api.domain.models.{QueueItem, Queue ⇒ ModelQueue}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Queue {

  case class QueueItemResponse(track: TrackResponse)
  def queueItemToQueueItemResponse(queueItem: QueueItem): QueueItemResponse = {
    QueueItemResponse(trackToTrackResponse(queueItem.item))
  }
  implicit val queueItemResponseWrites: Writes[QueueItemResponse] = {
    JsonNaming.snakecase(Json.writes[QueueItemResponse])
  }

  case class QueueResponse(items: Seq[QueueItemResponse])
  def queueToQueueResponse(queue: ModelQueue): QueueResponse = {
    QueueResponse(queue.items.map(queueItemToQueueItemResponse))
  }
  implicit val queueResponseWrites: Writes[QueueResponse] = {
    JsonNaming.snakecase(Json.writes[QueueResponse])
  }
}
