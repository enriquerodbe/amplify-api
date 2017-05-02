package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.controllers.dtos.User.authenticatedUserToUserResponse
import com.amplify.api.domain.logic.UserAuthLogic
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class UserAuthController @Inject()(
    userAuthLogic: UserAuthLogic,
    authHeadersUtil: AuthHeadersUtil,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp = Action.async(parse.empty) { implicit request ⇒
    authHeadersUtil.getAuthToken(request) match {
      case Success(authToken) ⇒
        val eventualUser = userAuthLogic.signUp(authToken)
        eventualUser.map(user ⇒ Ok(Json.toJson(authenticatedUserToUserResponse(user))))
      case Failure(exception) ⇒
        Future.failed(exception)
    }
  }
}
