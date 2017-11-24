package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.controllers.dtos.Image.{ImageResponse, imageToImageResponse}
import com.amplify.api.domain.models.{PlaylistInfo, Track, Playlist ⇒ ModelPlaylist}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Playlist {

  case class PlaylistTrackResponse(
      name: String,
      contentProvider: String,
      contentIdentifier: String,
      album: AlbumResponse)
  def trackToPlaylistTrackResponse(track: Track): PlaylistTrackResponse = {
    PlaylistTrackResponse(
      track.name,
      track.identifier.contentProvider.toString,
      track.identifier.identifier,
      albumToAlbumResponse(track.album))
  }
  implicit val playlistTrackResponseWrites: Writes[PlaylistTrackResponse] = {
    JsonNaming.snakecase(Json.writes[PlaylistTrackResponse])
  }

  case class PlaylistInfoResponse(name: String, identifier: String, images: Seq[ImageResponse])
  def playlistInfoToPlaylistInfoResponse(info: PlaylistInfo): PlaylistInfoResponse = {
    PlaylistInfoResponse(
      info.name,
      info.identifier,
      info.images.map(imageToImageResponse))
  }
  implicit val playlistInfoResponseWrites: Writes[PlaylistInfoResponse] = {
    JsonNaming.snakecase(Json.writes[PlaylistInfoResponse])
  }

  case class PlaylistResponse(info: PlaylistInfoResponse, tracks: Seq[PlaylistTrackResponse])
  def playlistToPlaylistResponse(playlist: ModelPlaylist): PlaylistResponse = {
    PlaylistResponse(
      playlistInfoToPlaylistInfoResponse(playlist.info),
      playlist.tracks.map(trackToPlaylistTrackResponse))
  }
  implicit val playlistResponseWrites: Writes[PlaylistResponse] = {
    JsonNaming.snakecase(Json.writes[PlaylistResponse])
  }

  case class PlaylistRequest(identifier: String)
  implicit val playlistRequestReads: Reads[PlaylistRequest] = {
    JsonNaming.snakecase(Json.reads[PlaylistRequest])
  }
}
