package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.User.{UserResponse, authenticatedUserToUserResponse}
import com.amplify.api.domain.models.AuthenticatedVenue
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Venue {

  case class SignUpReq(name: String)
  implicit val signUpReads: Reads[SignUpReq] = JsonNaming.snakecase(Json.reads[SignUpReq])

  case class VenueResponse(user: UserResponse, name: String)
  def venueToVenueResponse(venue: AuthenticatedVenue): VenueResponse = {
    VenueResponse(authenticatedUserToUserResponse(venue.user), venue.name)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = {
    JsonNaming.snakecase(Json.writes[VenueResponse])
  }
}
