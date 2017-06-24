package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.domain.models.{Track ⇒ ModelTrack}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Track {

  case class TrackResponse(
      name: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse)
  def trackToTrackResponse(track: ModelTrack): TrackResponse = {
    TrackResponse(track.name,
      track.contentProviderIdentifier.contentProvider.toString,
      track.contentProviderIdentifier.identifier,
      albumToAlbumResponse(track.album))
  }
  implicit val trackResponseWrites: Writes[TrackResponse] = {
    JsonNaming.snakecase(Json.writes[TrackResponse])
  }

  case class AddTrackRequest(identifier: String)
  implicit val addTrackRequestReads: Reads[AddTrackRequest] = {
    JsonNaming.snakecase(Json.reads[AddTrackRequest])
  }
}
