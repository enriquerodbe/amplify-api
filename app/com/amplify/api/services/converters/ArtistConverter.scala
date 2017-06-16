package com.amplify.api.services.converters

import com.amplify.api.domain.models.Artist
import com.amplify.api.services.external.models.ArtistData

object ArtistConverter {

  def artistDataToArtist(artistData: ArtistData): Artist = Artist(artistData.name)
}
