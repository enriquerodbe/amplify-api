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
    Future.successful(ClientErrorResponse(AppExceptionCode.BadRequest, message))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Server error occurred: ${exception.getMessage}", exception)
    lazy val errorMsg = "Internal server error"
    exception match {
      case ex: UnauthorizedException ⇒
        val response = Unauthorized(Json.toJson(ClientErrorResponse(ex.code, ex.message)))
        Future.successful(response.withHeaders(Http.HeaderNames.WWW_AUTHENTICATE → "Bearer"))
      case ex: BadRequestException ⇒
        Future.successful(BadRequest(Json.toJson(ClientErrorResponse(ex.code, ex.message))))
      case ex: InternalException ⇒
        Future.successful(ServerErrorResponse(ex.code, errorMsg))
      case _ ⇒
        Future.successful(ServerErrorResponse(message = errorMsg))
    }
  }
}
