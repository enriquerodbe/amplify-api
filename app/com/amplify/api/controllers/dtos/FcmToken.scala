package com.amplify.api.controllers.dtos

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads}

object FcmToken {

  case class FcmTokenRequest(token: String)
  implicit val fcmTokenRequestReads: Reads[FcmTokenRequest] = {
    JsonNaming.snakecase(Json.reads[FcmTokenRequest])
  }
}
