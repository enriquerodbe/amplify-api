package com.amplify.api.controllers.dtos

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Format, Json}

object VenueAuth {

  case class SignUpReq(name: String)
  implicit val signUpFormat: Format[SignUpReq] = JsonNaming.snakecase(Json.format[SignUpReq])
}
