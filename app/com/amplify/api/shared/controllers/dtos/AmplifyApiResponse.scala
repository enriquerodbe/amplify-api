package com.amplify.api.shared.controllers.dtos

import com.amplify.api.shared.exceptions.AppExceptionCode
import com.amplify.api.shared.exceptions.AppExceptionCode.AppExceptionCode
import play.api.libs.json._
import play.api.mvc.Results.Status
import play.api.mvc.{Result, Results}
import play.mvc.Http
import scala.language.implicitConversions

sealed trait AmplifyApiResponse {

  def toJson: JsValue

  def toResult: Result
}

abstract class SuccessfulResponse extends AmplifyApiResponse {

  override def toResult: Result = Results.Ok(toJson)
}

case class SeqResponse(seq: Seq[SuccessfulResponse]) extends SuccessfulResponse {

  override def toJson: JsValue = JsArray(seq.map(_.toJson))
}

sealed abstract class FailedResponse(
    val status: Int,
    val code: AppExceptionCode,
    val message: String) extends AmplifyApiResponse {

  override def toJson: JsValue = Json.obj("code" → JsNumber(code.id), "message" → message)

  override def toResult: Result = Status(status)(toJson)
}

case class UnauthorizedErrorResponse(
    override val code: AppExceptionCode,
    override val message: String,
    override val status: Int = Http.Status.UNAUTHORIZED)
  extends FailedResponse(status, code, message)

case class ClientErrorResponse(
    override val code: AppExceptionCode,
    override val message: String,
    override val status: Int = Http.Status.BAD_REQUEST)
  extends FailedResponse(status, code, message)

case class ServerErrorResponse(
    override val code: AppExceptionCode = AppExceptionCode.Unexpected,
    override val message: String,
    override val status: Int = Http.Status.INTERNAL_SERVER_ERROR)
  extends FailedResponse(status, code, message)

object AmplifyApiResponse {

  implicit def amplifyApiResponseToResult[T](
      t: T)(
      implicit toResponse: T ⇒ AmplifyApiResponse): Result = toResponse(t).toResult

  implicit def seqToSuccessfulResponse(seq: Seq[SuccessfulResponse]): SeqResponse = SeqResponse(seq)
}
