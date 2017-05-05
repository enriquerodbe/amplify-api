package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos.{PlaylistItem, Playlists, User}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

object JsonConverters {

  implicit val userReads = JsonNaming.snakecase(Json.reads[User])

  implicit val playlistItemReads = JsonNaming.snakecase(Json.reads[PlaylistItem])

  implicit val playlistsReads = JsonNaming.snakecase(Json.reads[Playlists])
}
