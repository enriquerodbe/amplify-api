package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.controllers.dtos.SuccessfulResponse
import com.amplify.api.controllers.dtos.User.authenticatedUserToUserResponse
import com.amplify.api.domain.logic.UserAuthLogic
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class UserAuthController @Inject()(
    cc: ControllerComponents,
    userAuthLogic: UserAuthLogic,
    authHeadersUtil: AuthHeadersUtil,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) {

  def signUp = Action.async(parse.empty) { request ⇒
    authHeadersUtil.getAuthToken(request) match {
      case Success(authToken) ⇒
        val eventualUser = userAuthLogic.signUp(authToken)
        eventualUser.map { user ⇒
          SuccessfulResponse(authenticatedUserToUserResponse(user))
        }
      case Failure(exception) ⇒
        Future.failed(exception)
    }
  }
}
