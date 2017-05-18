package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Track ⇒ ModelTrack}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Track {

  case class TrackResponse(name: String, contentProviderIdentifier: String)
  def trackToTrackResponse(track: ModelTrack): TrackResponse = {
    TrackResponse(track.name, track.contentProviderIdentifier)
  }
  implicit val trackResponseWrites: Writes[TrackResponse] = {
    JsonNaming.snakecase(Json.writes[TrackResponse])
  }
}
