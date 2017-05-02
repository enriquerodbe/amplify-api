package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.PrimitivesJsonConverters._
import com.amplify.api.controllers.dtos.User.{UserResponse, authenticatedUserToUserResponse}
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Venue {

  case class SignUpReq(name: String)
  implicit val signUpReads: Reads[SignUpReq] = JsonNaming.snakecase(Json.reads[SignUpReq])

  case class VenueResponse(user: UserResponse, name: Name)
  def venueToVenueResponse(venue: Venue): VenueResponse = {
    VenueResponse(authenticatedUserToUserResponse(venue.user), venue.name)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = Json.writes[VenueResponse]
}
