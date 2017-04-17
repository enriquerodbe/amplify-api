package com.amplify.api.exceptions

import com.google.inject.Provider
import javax.inject.{Inject, Singleton}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}
import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with Results {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case ex: BadRequestException ⇒ Future.successful(BadRequest(ex.getMessage))
      case ex: InternalException ⇒ Future.successful(InternalServerError(ex.getMessage))
      case _ ⇒ super.onServerError(request, exception)
    }
  }
}
