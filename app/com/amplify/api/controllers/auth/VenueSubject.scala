package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.Venue

case class VenueSubject(
    venue: Venue,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  override def identifier: String = venue.identifier.toString
}
