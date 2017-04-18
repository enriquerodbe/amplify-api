package com.amplify.api.controllers

import com.iheart.playSwagger.SwaggerSpecGenerator
import javax.inject.Inject
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off public.methods.have.type
class SwaggerController @Inject()(implicit ec: ExecutionContext) extends Controller {
  implicit val cl = getClass.getClassLoader
  private lazy val generator = SwaggerSpecGenerator("com.amplify.api.controllers.dtos")

  def specs = Action.async { _ ⇒
    Future.fromTry(generator.generate()).map(Ok(_))
  }
}
