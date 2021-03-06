package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.{Access, Refresh, Token, _}

case class Venue(
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    refreshToken: Token[Refresh],
    accessToken: Token[Access]) {

  val contentProviders: ContentProvider = ContentProvider.Spotify
}

case class VenueData(
    identifier: AuthProviderIdentifier,
    name: Name,
    refreshToken: Token[Refresh],
    accessToken: Token[Access])
