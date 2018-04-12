package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.primitives.{Name, Uid}

case class Venue(name: Name, uid: Uid, identifier: AuthProviderIdentifier) {

  def contentProviders: ContentProvider = ContentProvider.Spotify
}

case class VenueReq(venue: Venue, authToken: AuthToken) {

  def name: Name = venue.name

  def uid: Uid = venue.uid

  def identifier: AuthProviderIdentifier = venue.identifier

  def contentProviders: ContentProvider = venue.contentProviders
}
