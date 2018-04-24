package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Venue ⇒ VenueModel}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object Venue extends DtosDefinition {

  implicit val venueResponseWrites: Writes[VenueResponse] = Json.writes[VenueResponse]
  case class VenueResponse(name: String, uid: String) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def venueToVenueResponse(venue: VenueModel): VenueResponse = {
    VenueResponse(venue.name.value, venue.uid.value)
  }

  case class VenueSignUpRequest(name: String, code: String)
  implicit val venueRequestReads: Reads[VenueSignUpRequest] = (
    (JsPath \ "name").read[String](minLength[String](1)) and
    (JsPath \ "authorization_code").read[String](minLength[String](1))
  )(VenueSignUpRequest.apply _)
}
