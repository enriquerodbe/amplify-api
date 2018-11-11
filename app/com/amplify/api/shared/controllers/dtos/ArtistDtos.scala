package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.Artist
import play.api.libs.json.{Json, Writes}

object ArtistDtos extends DtosDefinition {

  case class ArtistResponse(name: String)
  def artistToArtistResponse(artist: Artist): ArtistResponse = {
    ArtistResponse(artist.name.value)
  }
  implicit val artistResponseWrites: Writes[ArtistResponse] = Json.writes[ArtistResponse]
}
