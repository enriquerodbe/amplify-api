package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Artist ⇒ ModelArtist}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Artist {

  case class ArtistResponse(name: String)
  def artistToArtistResponse(artist: ModelArtist): ArtistResponse = ArtistResponse(artist.name)
  implicit val artistResponseWrites: Writes[ArtistResponse] = {
    JsonNaming.snakecase(Json.writes[ArtistResponse])
  }
}
