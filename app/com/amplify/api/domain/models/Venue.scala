package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Name, Token, Uid}

case class Venue(
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    fcmToken: Option[Token]) {

  def contentProviders: ContentProviderType = ContentProviderType.Spotify
}

case class VenueReq(venue: Venue, authToken: AuthToken) {

  def name: Name = venue.name

  def uid: Uid = venue.uid

  def identifier: AuthProviderIdentifier = venue.identifier

  def contentProviders: ContentProviderType = venue.contentProviders
}
