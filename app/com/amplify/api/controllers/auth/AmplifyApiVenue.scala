package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.{VenueReq, Venue}

case class AmplifyApiVenue(
    venueReq: VenueReq,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  def venue: Venue = venueReq.venue

  override def identifier: String = venueReq.identifier.toString
}
