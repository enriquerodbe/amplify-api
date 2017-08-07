package com.amplify.api.controllers.dtos

import play.api.libs.json.{Json, Reads}

object FcmToken extends DtosDefinition {

  case class FcmTokenRequest(token: String)
  implicit val fcmTokenRequestReads: Reads[FcmTokenRequest] = Json.reads[FcmTokenRequest]
}
