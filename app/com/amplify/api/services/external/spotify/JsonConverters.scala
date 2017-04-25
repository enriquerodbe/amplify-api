package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos.{PlaylistItem, Playlists, User}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

object JsonConverters {

  implicit val userFormat = JsonNaming.snakecase(Json.format[User])

  implicit val playlistItemFormat = JsonNaming.snakecase(Json.format[PlaylistItem])

  implicit val playlistsFormat = JsonNaming.snakecase(Json.format[Playlists])
}
