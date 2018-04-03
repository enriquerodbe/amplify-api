package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Artist.{ArtistResponse, artistToArtistResponse}
import com.amplify.api.controllers.dtos.Image.{ImageResponse, imageToImageResponse}
import com.amplify.api.domain.models.{Album ⇒ ModelAlbum}
import play.api.libs.json.{Json, Writes}

object Album extends DtosDefinition {

  case class AlbumResponse(name: String, artists: Seq[ArtistResponse], images: Seq[ImageResponse])
  def albumToAlbumResponse(album: ModelAlbum): AlbumResponse = {
    AlbumResponse(
      album.name.value,
      album.artists.map(artistToArtistResponse),
      album.images.map(imageToImageResponse))
  }
  implicit val albumResponseWrites: Writes[AlbumResponse] = Json.writes[AlbumResponse]
}
