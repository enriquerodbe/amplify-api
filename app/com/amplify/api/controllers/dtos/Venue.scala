package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Venue ⇒ VenueModel}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Venue {

  case class VenueResponse(name: String, uid: String)
  def venueToVenueResponse(venue: VenueModel): VenueResponse = {
    VenueResponse(venue.name, venue.uid)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = {
    JsonNaming.snakecase(Json.writes[VenueResponse])
  }

  case class VenueRequest(name: String)
  implicit val venueRequestReads: Reads[VenueRequest] = {
    JsonNaming.snakecase(Json.reads[VenueRequest])
  }

  case class AddTrackRequest(trackIdentifier: String)
  implicit val addTrackRequestReads: Reads[AddTrackRequest] = {
    JsonNaming.snakecase(Json.reads[AddTrackRequest])
  }
}
