package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Artist.{ArtistResponse, artistToArtistResponse}
import com.amplify.api.controllers.dtos.Playlist.{ImageResponse, imageToImageResponse}
import com.amplify.api.domain.models.{Album ⇒ ModelAlbum}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Album {

  case class AlbumResponse(name: String, artists: Seq[ArtistResponse], images: Seq[ImageResponse])
  def albumToAlbumResponse(album: ModelAlbum): AlbumResponse = {
    AlbumResponse(
      album.name,
      album.artists.map(artistToArtistResponse),
      album.images.map(imageToImageResponse))
  }
  implicit val albumResponseWrites: Writes[AlbumResponse] = {
    JsonNaming.snakecase(Json.writes[AlbumResponse])
  }
}
