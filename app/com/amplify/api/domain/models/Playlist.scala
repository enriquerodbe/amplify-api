package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Playlist(name: Name, identifier: ContentProviderIdentifier, images: Seq[Image])

case class Image(url: String, height: Option[Int], width: Option[Int])
