package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.domain.models.{QueueItem, Queue ⇒ ModelQueue}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Queue {

  case class QueueTrackResponse(
      name: String,
      priority: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse)
  def itemToQueueTrackResponse(item: QueueItem): QueueTrackResponse = {
    QueueTrackResponse(
      item.track.name,
      item.itemType.toString,
      item.track.identifier.contentProvider.toString,
      item.track.identifier.identifier,
      albumToAlbumResponse(item.track.album))
  }
  implicit val queueTrackResponseWrites: Writes[QueueTrackResponse] = {
    JsonNaming.snakecase(Json.writes[QueueTrackResponse])
  }

  case class CurrentTrackResponse(
      name: String,
      priority: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse,
      position: Int)
  def itemToCurrentTrackResponse(item: QueueItem, index: Int): CurrentTrackResponse = {
    CurrentTrackResponse(
      item.track.name,
      item.itemType.toString,
      item.track.identifier.contentProvider.toString,
      item.track.identifier.identifier,
      albumToAlbumResponse(item.track.album),
      index)
  }
  implicit val currentTrackResponseWrites: Writes[CurrentTrackResponse] = {
    JsonNaming.snakecase(Json.writes[CurrentTrackResponse])
  }

  case class QueueResponse(
      currentPlaylist: Option[String],
      currentTrack: Option[CurrentTrackResponse],
      tracks: Seq[QueueTrackResponse])
  def queueToQueueResponse(queue: ModelQueue): QueueResponse = {
    QueueResponse(
      queue.currentPlaylist.map(_.info.identifier.toString),
      queue.currentItem.map { item =>
        val currentTrackIndex = queue.allItems.indexOf(item)
        itemToCurrentTrackResponse(item, currentTrackIndex)
      },
      queue.allItems.map(itemToQueueTrackResponse))
  }
  implicit val queueResponseWrites: Writes[QueueResponse] = {
    JsonNaming.snakecase(Json.writes[QueueResponse])
  }

  case class AddTrackRequest(identifier: String)
  implicit val addTrackRequestReads: Reads[AddTrackRequest] = {
    JsonNaming.snakecase(Json.reads[AddTrackRequest])
  }
}
