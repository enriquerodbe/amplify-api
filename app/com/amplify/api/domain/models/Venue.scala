package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Id, Name, Token, Uid}

trait Venue {

  def name: Name

  def uid: Uid
}

case class UnauthenticatedVenue(
    id: Id,
    name: Name,
    uid: Uid,
    fcmToken: Option[Token]) extends Venue

case class AuthenticatedVenue(
    user: AuthenticatedUser,
    unauthenticated: UnauthenticatedVenue) extends Venue {

  override def name: Name = unauthenticated.name

  override def uid: Uid = unauthenticated.uid

  def id: Id = unauthenticated.id

  def contentProviders: ContentProviderType = ContentProviderType.Spotify
}

case class AuthenticatedVenueReq(venue: AuthenticatedVenue, authToken: AuthToken) extends Venue {

  override def name: Name = venue.name

  override def uid: Uid = venue.uid

  def user: AuthenticatedUser = venue.user

  def id: Id = venue.id

  def userIdentifier: AuthProviderIdentifier = user.identifier

  def contentProviders: ContentProviderType = venue.contentProviders

  def unauthenticated: UnauthenticatedVenue = venue.unauthenticated
}
