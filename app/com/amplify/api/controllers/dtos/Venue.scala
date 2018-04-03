package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Venue ⇒ VenueModel}
import play.api.libs.json.{Json, Reads, Writes}

object Venue extends DtosDefinition {

  case class VenueResponse(name: String, uid: String)
  def venueToVenueResponse(venue: VenueModel): VenueResponse = {
    VenueResponse(venue.name.value, venue.uid.value)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = Json.writes[VenueResponse]

  case class VenueRequest(name: String)
  implicit val venueRequestReads: Reads[VenueRequest] = Json.reads[VenueRequest]
}
