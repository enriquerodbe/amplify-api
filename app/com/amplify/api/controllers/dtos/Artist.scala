package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Artist ⇒ ModelArtist}
import play.api.libs.json.{Json, Writes}

object Artist extends DtosDefinition {

  case class ArtistResponse(name: String)
  def artistToArtistResponse(artist: ModelArtist): ArtistResponse = {
    ArtistResponse(artist.name.value)
  }
  implicit val artistResponseWrites: Writes[ArtistResponse] = Json.writes[ArtistResponse]
}
