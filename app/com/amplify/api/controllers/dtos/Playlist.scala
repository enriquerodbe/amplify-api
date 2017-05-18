package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Playlist ⇒ ModelPlaylist}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Playlist {

  case class PlaylistResponse(name: String, identifier: String)
  def playlistToPlaylistResponse(playlist: ModelPlaylist): PlaylistResponse = {
    PlaylistResponse(playlist.name, playlist.identifier)
  }
  implicit val playlistResponseWrites: Writes[PlaylistResponse] = {
    JsonNaming.snakecase(Json.writes[PlaylistResponse])
  }

  case class PlaylistRequest(identifier: String)
  implicit val playlistRequestReads: Reads[PlaylistRequest] = {
    JsonNaming.snakecase(Json.reads[PlaylistRequest])
  }
}
