package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.domain.models.QueueItem
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Track {

  sealed trait TrackResponse {
    def name: String
    def priority: String
    def contentProvider: String
    def contentIdentifier: String
    def album: AlbumResponse
  }

  case class QueueTrackResponse(
      name: String,
      priority: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse) extends TrackResponse

  case class CurrentTrackResponse(
      name: String,
      priority: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse,
      position: Int) extends TrackResponse

  def itemToQueueTrackResponse(item: QueueItem): QueueTrackResponse = {
    QueueTrackResponse(item.track.name,
      item.itemType.toString,
      item.track.identifier.contentProvider.toString,
      item.track.identifier.identifier,
      albumToAlbumResponse(item.track.album))
  }
  def itemToCurrentTrackResponse(item: QueueItem, index: Int): CurrentTrackResponse = {
    CurrentTrackResponse(item.track.name,
      item.itemType.toString,
      item.track.identifier.contentProvider.toString,
      item.track.identifier.identifier,
      albumToAlbumResponse(item.track.album),
      index)
  }
  implicit val queueTrackResponseWrites: Writes[QueueTrackResponse] = {
    JsonNaming.snakecase(Json.writes[QueueTrackResponse])
  }
  implicit val currentTrackResponseWrites: Writes[CurrentTrackResponse] = {
    JsonNaming.snakecase(Json.writes[CurrentTrackResponse])
  }

  case class AddTrackRequest(identifier: String)
  implicit val addTrackRequestReads: Reads[AddTrackRequest] = {
    JsonNaming.snakecase(Json.reads[AddTrackRequest])
  }
}
