package com.amplify.api.services.converters

import com.amplify.api.domain.models.Playlist
import com.amplify.api.services.external.PlaylistData

object PlaylistConverter {

  def playlistDataToPlaylist(playlistData: PlaylistData): Playlist = {
    Playlist(playlistData.name, playlistData.identifier)
  }
}
