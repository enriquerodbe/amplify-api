package com.amplify.api.controllers.dtos

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Format, Json}

object VenueManagement {

  case class SignUp(name: String)
  implicit val signUpFormat: Format[SignUp] = JsonNaming.snakecase(Json.format[SignUp])
}
