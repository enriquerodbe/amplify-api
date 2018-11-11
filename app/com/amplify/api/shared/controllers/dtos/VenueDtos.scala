package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.Venue
import play.api.libs.json.Reads.minLength
import play.api.libs.json._

object VenueDtos extends DtosDefinition {

  implicit val venueResponseWrites: Writes[VenueResponse] = Json.writes[VenueResponse]
  case class VenueResponse(name: String, uid: String, accessToken: String)
    extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def venueToVenueResponse(venue: Venue): VenueResponse = {
    VenueResponse(venue.name.value, venue.uid.value, venue.accessToken.value)
  }

  case class VenueSignInRequest(code: String)
  implicit val venueRequestReads: Reads[VenueSignInRequest] = {
    (JsPath \ "code").read[String](minLength[String](1)).map(VenueSignInRequest)
  }
}
