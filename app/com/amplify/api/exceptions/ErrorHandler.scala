package com.amplify.api.exceptions

import com.amplify.api.controllers.dtos.ErrorResponse
import com.google.inject.Provider
import javax.inject.{Inject, Singleton}
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper, UsefulException}
import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with Results {

  override def onProdServerError(
      request: RequestHeader,
      exception: UsefulException): Future[Result] = {
    onError(request, exception.cause, s"[${exception.id}] ${exception.title}")
  }

  override protected def onDevServerError(
      request: RequestHeader,
      exception: UsefulException): Future[Result] = {
    val unexpectedMessage = s"[${exception.id}] ${exception.title}: ${exception.description}"
    onError(request, exception.cause, unexpectedMessage)
  }

  private def onError(
      request: RequestHeader,
      exception: Throwable,
      unexpectedMessage: String) = exception match {
    case ex: ForbiddenException ⇒
      Future.successful(Forbidden(Json.toJson(ErrorResponse(ex.code, ex.message))))
    case ex: BadRequestException ⇒
      Future.successful(BadRequest(Json.toJson(ErrorResponse(ex.code, ex.message))))
    case ex: InternalException ⇒
      Future.successful(InternalServerError(Json.toJson(ErrorResponse(ex.code, ex.message))))
    case _ ⇒
      val body = Json.toJson(ErrorResponse(AppExceptionCode.Unexpected, unexpectedMessage))
      Future.successful(InternalServerError(body))
  }
}
