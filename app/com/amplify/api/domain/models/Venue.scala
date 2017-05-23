package com.amplify.api.domain.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.primitives.Name

trait Venue {

  def name: Name
}

case class UnauthenticatedVenue(id: Id[Venue], name: Name) extends Venue

case class AuthenticatedVenue(id: Id[Venue], user: AuthenticatedUser, name: Name) extends Venue {

  def toUnauthenticated: UnauthenticatedVenue = UnauthenticatedVenue(id, name)
}

case class AuthenticatedVenueReq(venue: AuthenticatedVenue, authToken: AuthToken) extends Venue {

  override def name: Name = venue.name

  def user: AuthenticatedUser = venue.user
}

case class VenueReq(name: Name) extends Venue
