package com.amplify.api.services.converters

import com.amplify.api.domain.models.{Image, Playlist}
import com.amplify.api.services.external.models.{ImageData, PlaylistData}

object PlaylistConverter {

  def playlistDataToPlaylist(playlistData: PlaylistData): Playlist = {
    Playlist(playlistData.name, playlistData.identifier, playlistData.images.map(imageDataToImage))
  }

  def imageDataToImage(imageData: ImageData): Image = {
    Image(imageData.url, imageData.height, imageData.width)
  }
}
