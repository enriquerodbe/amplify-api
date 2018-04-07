package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Venue ⇒ VenueModel}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads, Writes}

object Venue extends DtosDefinition {

  case class VenueResponse(name: String, uid: String)
  def venueToVenueResponse(venue: VenueModel): VenueResponse = {
    VenueResponse(venue.name.value, venue.uid.value)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = (
    (JsPath \ "name").write[String] and
    (JsPath \ "uid").write[String]
  )(unlift(VenueResponse.unapply))

  case class VenueRequest(name: String)
  implicit val venueRequestReads: Reads[VenueRequest] = {
    (JsPath \ "name").read[String](minLength[String](1)).map(VenueRequest)
  }
}
