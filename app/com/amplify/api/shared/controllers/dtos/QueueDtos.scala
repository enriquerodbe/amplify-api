package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.{Queue, QueueItem}
import com.amplify.api.shared.controllers.dtos.AlbumDtos.{AlbumResponse, albumToAlbumResponse}
import play.api.libs.json.{JsValue, Json, Reads, Writes}

object QueueDtos extends DtosDefinition {

  case class QueueTrackResponse(
      name: String,
      priority: String,
      identifier: String,
      album: AlbumResponse) extends SuccessfulResponse {
    override def toJson: JsValue = Json.toJson(this)
  }
  def itemToQueueTrackResponse(item: QueueItem): QueueTrackResponse = {
    QueueTrackResponse(
      item.track.name.value,
      item.itemType.toString,
      item.track.identifier.toString,
      albumToAlbumResponse(item.track.album))
  }
  implicit val queueTrackResponseWrites: Writes[QueueTrackResponse] = {
    Json.writes[QueueTrackResponse]
  }

  case class CurrentTrackResponse(
      name: String,
      priority: String,
      identifier: String,
      album: AlbumResponse,
      position: Int)
  def itemToCurrentTrackResponse(item: QueueItem, index: Int): CurrentTrackResponse = {
    CurrentTrackResponse(
      item.track.name.value,
      item.itemType.toString,
      item.track.identifier.toString,
      albumToAlbumResponse(item.track.album),
      index)
  }
  implicit val currentTrackResponseWrites: Writes[CurrentTrackResponse] = {
    Json.writes[CurrentTrackResponse]
  }

  case class QueueResponse(
      allowedPlaylist: Option[String],
      currentTrack: Option[CurrentTrackResponse],
      tracks: Seq[QueueTrackResponse]) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def queueToQueueResponse(queue: Queue): QueueResponse = {
    QueueResponse(
      allowedPlaylist = queue.allowedPlaylist.map(_.info.identifier.toString),
      currentTrack = queue.currentItem.map { item =>
        val currentTrackIndex = queue.allItems.indexOf(item)
        itemToCurrentTrackResponse(item, currentTrackIndex)
      },
      tracks = queue.allItems.map(itemToQueueTrackResponse))
  }
  implicit val queueResponseWrites: Writes[QueueResponse] = Json.writes[QueueResponse]

  case class AddTrackRequest(identifier: String)
  implicit val addTrackRequestReads: Reads[AddTrackRequest] = Json.reads[AddTrackRequest]
}
