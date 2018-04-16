package com.amplify.api.exceptions

import com.amplify.api.controllers.dtos.AmplifyApiResponse._
import com.amplify.api.controllers.dtos.{ClientErrorResponse, ServerErrorResponse}
import com.google.inject.Provider
import javax.inject.{Inject, Singleton}
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.routing.Router
import play.api.{Configuration, Environment, Logger, OptionalSourceMapper}
import play.mvc.Http
import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router])
  extends HttpErrorHandler with Results {

  private val logger = Logger(classOf[ErrorHandler])

  override def onClientError(
      request: RequestHeader,
      statusCode: Int,
      message: String): Future[Result] = {
    Future.successful(ClientErrorResponse(AppExceptionCode.BadRequest, message, statusCode))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case ex: UnauthorizedException ⇒
        val response = Unauthorized(Json.toJson(ClientErrorResponse(ex.code, ex.message)))
        Future.successful(response.withHeaders(Http.HeaderNames.WWW_AUTHENTICATE → "Bearer"))
      case ex: BadRequestException ⇒
        Future.successful(BadRequest(Json.toJson(ClientErrorResponse(ex.code, ex.message))))
      case ex ⇒
        logger.error(s"Server error occurred: ${exception.getMessage}", exception)
        val message = "Internal server error"
        val response = ex match {
          case ex: InternalException ⇒ ServerErrorResponse(ex.code, message)
          case _ ⇒ ServerErrorResponse(message = message)
        }
        Future.successful(response)
    }
  }
}
