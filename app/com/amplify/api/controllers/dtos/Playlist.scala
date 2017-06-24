package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Image, Playlist ⇒ ModelPlaylist}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Playlist {

  case class ImageResponse(url: String, height: Option[Int], width: Option[Int])
  def imageToImageResponse(image: Image): ImageResponse = {
    ImageResponse(image.url, image.height, image.width)
  }
  implicit val imageResponseWrites: Writes[ImageResponse] = {
    JsonNaming.snakecase(Json.writes[ImageResponse])
  }

  case class PlaylistResponse(name: String, identifier: String, images: Seq[ImageResponse])
  def playlistToPlaylistResponse(playlist: ModelPlaylist): PlaylistResponse = {
    PlaylistResponse(playlist.name, playlist.identifier, playlist.images.map(imageToImageResponse))
  }
  implicit val playlistResponseWrites: Writes[PlaylistResponse] = {
    JsonNaming.snakecase(Json.writes[PlaylistResponse])
  }

  case class PlaylistRequest(identifier: String)
  implicit val playlistRequestReads: Reads[PlaylistRequest] = {
    JsonNaming.snakecase(Json.reads[PlaylistRequest])
  }
}
