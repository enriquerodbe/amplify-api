package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Id, Name, Uid}

trait Venue {

  def name: Name

  def uid: Uid
}

case class UnauthenticatedVenue(
    id: Id,
    name: Name,
    uid: Uid) extends Venue

case class AuthenticatedVenue(
    user: AuthenticatedUser,
    unauthenticated: UnauthenticatedVenue) extends Venue {

  override def name: Name = unauthenticated.name

  override def uid: Uid = unauthenticated.uid

  def id: Id = unauthenticated.id
}

case class AuthenticatedVenueReq(venue: AuthenticatedVenue, authToken: AuthToken) extends Venue {

  override def name: Name = venue.name

  override def uid: Uid = venue.uid

  def user: AuthenticatedUser = venue.user

  def id: Id = venue.id

  def userIdentifier: ContentProviderIdentifier = user.identifier
}
