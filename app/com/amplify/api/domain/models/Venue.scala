package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

trait Venue {

  def name: Name
}

case class AuthenticatedVenue(user: AuthenticatedUser, name: Name)

case class VenueReq(name: Name) extends Venue
