package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.Album
import com.amplify.api.shared.controllers.dtos.ArtistDtos.{ArtistResponse, artistToArtistResponse}
import com.amplify.api.shared.controllers.dtos.ImageDtos.{ImageResponse, imageToImageResponse}
import play.api.libs.json.{Json, Writes}

object AlbumDtos extends DtosDefinition {

  case class AlbumResponse(name: String, artists: Seq[ArtistResponse], images: Seq[ImageResponse])
  def albumToAlbumResponse(album: Album): AlbumResponse = {
    AlbumResponse(
      album.name.value,
      album.artists.map(artistToArtistResponse),
      album.images.map(imageToImageResponse))
  }
  implicit val albumResponseWrites: Writes[AlbumResponse] = Json.writes[AlbumResponse]
}
