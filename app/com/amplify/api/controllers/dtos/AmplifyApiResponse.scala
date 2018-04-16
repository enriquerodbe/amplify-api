package com.amplify.api.controllers.dtos

import com.amplify.api.exceptions.AppExceptionCode
import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode
import play.api.libs.json._
import play.api.mvc.Results.Status
import play.api.mvc.{Result, Results}
import play.mvc.Http
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

sealed abstract class AmplifyApiResponse[T](val status: Int, val details: T)

case class SuccessfulResponse[T](response: T) extends AmplifyApiResponse(Http.Status.OK, response)

sealed abstract class FailedResponse(status: Int, val code: AppExceptionCode, message: String)
  extends AmplifyApiResponse[String](status, message)

case class ClientErrorResponse(
    override val code: AppExceptionCode,
    message: String,
    override val status: Int = Http.Status.BAD_REQUEST)
  extends FailedResponse(status, code, message)

case class ServerErrorResponse(
    override val code: AppExceptionCode = AppExceptionCode.Unexpected,
    message: String,
    override val status: Int = Http.Status.INTERNAL_SERVER_ERROR)
  extends FailedResponse(status, code, message)

object AmplifyApiResponse {

  implicit def amplifyApiResponseWrites[T](
      implicit writes: Writes[T]): Writes[AmplifyApiResponse[T]] = Writes {
    case SuccessfulResponse(o) ⇒ Json.toJson(o)
    case f: FailedResponse ⇒ Json.obj("code" → JsNumber(f.code.id), "message" → f.details)
  }

  implicit def amplifyApiResponseToResult[T](
      response: AmplifyApiResponse[T])(
      implicit writes: Writes[T]): Result = {
    response match {
      case success: SuccessfulResponse[T] => Results.Ok(Json.toJson(success))
      case failed: FailedResponse => Status(failed.status)(Json.toJson(failed))
    }
  }
  implicit def amplifyApiResponseToResultFuture[T](
      response: Future[AmplifyApiResponse[T]])(
      implicit writes: Writes[T],
      ec: ExecutionContext): Future[Result] = {
    response.map(amplifyApiResponseToResult(_))
  }
}
