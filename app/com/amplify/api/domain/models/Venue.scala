package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.{Name, Token, Uid}

case class Venue(
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    accessToken: Token) {

  def contentProviders: ContentProvider = ContentProvider.Spotify
}
