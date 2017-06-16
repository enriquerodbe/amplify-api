package com.amplify.api.services.converters

import com.amplify.api.domain.models.Album
import com.amplify.api.services.converters.ArtistConverter.artistDataToArtist
import com.amplify.api.services.converters.PlaylistConverter.imageDataToImage
import com.amplify.api.services.external.models.AlbumData

object AlbumConverter {

  def albumDataToAlbum(albumData: AlbumData): Album = {
    Album(
      albumData.name,
      albumData.artists.map(artistDataToArtist),
      albumData.images.map(imageDataToImage))
  }
}
