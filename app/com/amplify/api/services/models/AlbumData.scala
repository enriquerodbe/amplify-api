package com.amplify.api.services.models

import com.amplify.api.domain.models.primitives.Name

case class AlbumData(name: Name, artists: Seq[ArtistData], images: Seq[ImageData])