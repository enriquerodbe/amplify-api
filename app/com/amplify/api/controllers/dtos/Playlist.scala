package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Album.{AlbumResponse, albumToAlbumResponse}
import com.amplify.api.controllers.dtos.Image.{ImageResponse, imageToImageResponse}
import com.amplify.api.domain.models.{PlaylistInfo, Track, Playlist ⇒ ModelPlaylist}
import play.api.libs.json.{Json, Reads, Writes}

object Playlist extends DtosDefinition {

  case class PlaylistTrackResponse(
      name: String,
      identifier: String,
      album: AlbumResponse)
  def trackToPlaylistTrackResponse(track: Track): PlaylistTrackResponse = {
    PlaylistTrackResponse(
      track.name,
      track.identifier,
      albumToAlbumResponse(track.album))
  }
  implicit val playlistTrackResponseWrites: Writes[PlaylistTrackResponse] = {
    Json.writes[PlaylistTrackResponse]
  }

  case class PlaylistInfoResponse(name: String, identifier: String, images: Seq[ImageResponse])
  def playlistInfoToPlaylistInfoResponse(info: PlaylistInfo): PlaylistInfoResponse = {
    PlaylistInfoResponse(
      info.name,
      info.identifier,
      info.images.map(imageToImageResponse))
  }
  implicit val playlistInfoResponseWrites: Writes[PlaylistInfoResponse] = {
    Json.writes[PlaylistInfoResponse]
  }

  case class PlaylistResponse(info: PlaylistInfoResponse, tracks: Seq[PlaylistTrackResponse])
  def playlistToPlaylistResponse(playlist: ModelPlaylist): PlaylistResponse = {
    PlaylistResponse(
      playlistInfoToPlaylistInfoResponse(playlist.info),
      playlist.tracks.map(trackToPlaylistTrackResponse))
  }
  implicit val playlistResponseWrites: Writes[PlaylistResponse] = Json.writes[PlaylistResponse]

  case class PlaylistRequest(identifier: String)
  implicit val playlistRequestReads: Reads[PlaylistRequest] = Json.reads[PlaylistRequest]
}
