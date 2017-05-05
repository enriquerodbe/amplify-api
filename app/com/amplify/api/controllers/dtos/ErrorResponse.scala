package com.amplify.api.controllers.dtos

import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode
import play.api.libs.json.{JsNumber, Json, Writes}

case class ErrorResponse(code: AppExceptionCode, message: String)

object ErrorResponse {

  implicit val appExceptionCodeWrites: Writes[AppExceptionCode] = Writes(c ⇒ JsNumber(c.id))

  implicit val errorResponseWrites: Writes[ErrorResponse] = Json.writes[ErrorResponse]
}
