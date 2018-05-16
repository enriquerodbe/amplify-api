package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives._

case class Venue(
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    refreshToken: Token[Refresh],
    accessToken: Token[Access]) {

  def contentProviders: ContentProvider = ContentProvider.Spotify
}

case class VenueData(
    identifier: AuthProviderIdentifier,
    name: Name,
    refreshToken: Token[Refresh],
    accessToken: Token[Access])
