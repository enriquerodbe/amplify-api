package com.amplify.api.services.converters

import com.amplify.api.domain.models.PlaylistInfo
import com.amplify.api.domain.models.Image
import com.amplify.api.services.models.{ImageData, PlaylistData}

object PlaylistConverter {

  def playlistDataToPlaylistInfo(playlistData: PlaylistData): PlaylistInfo = {
    PlaylistInfo(
      playlistData.name,
      playlistData.identifier,
      playlistData.images.map(imageDataToImage))
  }

  def imageDataToImage(imageData: ImageData): Image = {
    Image(imageData.url, imageData.height, imageData.width)
  }
}
