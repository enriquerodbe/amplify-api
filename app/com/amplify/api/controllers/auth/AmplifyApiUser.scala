package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.{AuthenticatedUser, AuthenticatedUserReq, AuthenticatedVenue, AuthenticatedVenueReq}

case class AmplifyApiUser(
    userReq: AuthenticatedUserReq,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  def user: AuthenticatedUser = userReq.user

  override def identifier: String = user.identifier.toString
}

case class AmplifyApiVenue(
    venueReq: AuthenticatedVenueReq,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  def venue: AuthenticatedVenue = venueReq.venue

  def user: AuthenticatedUser = venueReq.user

  override def identifier: String = venueReq.user.identifier.toString
}
