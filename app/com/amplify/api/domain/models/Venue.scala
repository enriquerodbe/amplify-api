package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Name, Uid}

case class Venue(name: Name, uid: Uid, identifier: AuthProviderIdentifier) {

  def contentProviders: ContentProviderType = ContentProviderType.Spotify
}

case class VenueReq(venue: Venue, authToken: AuthToken) {

  def name: Name = venue.name

  def uid: Uid = venue.uid

  def identifier: AuthProviderIdentifier = venue.identifier

  def contentProviders: ContentProviderType = venue.contentProviders
}
