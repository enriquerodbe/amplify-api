package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.domain.models.{Track ⇒ ModelTrack}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Track {

  sealed trait TrackResponse {
    def name: String
    def contentProvider: String
    def contentIdentifier: String
    def album: AlbumResponse
  }

  case class QueueTrackResponse(
      name: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse) extends TrackResponse

  case class CurrentTrackResponse(
      name: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse,
      position: Int) extends TrackResponse

  def trackToQueueTrackResponse(track: ModelTrack): QueueTrackResponse = {
    QueueTrackResponse(track.name,
      track.identifier.contentProvider.toString,
      track.identifier.identifier,
      albumToAlbumResponse(track.album))
  }
  def trackToCurrentTrackResponse(track: ModelTrack, index: Int): CurrentTrackResponse = {
    CurrentTrackResponse(track.name,
      track.identifier.contentProvider.toString,
      track.identifier.identifier,
      albumToAlbumResponse(track.album),
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
