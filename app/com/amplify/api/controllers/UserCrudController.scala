package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.SuccessfulResponse
import com.amplify.api.controllers.dtos.User._
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.Future

// scalastyle:off public.methods.have.type
class UserCrudController @Inject()(
    cc: ControllerComponents,
    val actionBuilder: ActionBuilders) extends AbstractController(cc) with AuthenticatedRequests {

  def retrieveCurrent() = authenticatedUser() { request ⇒
    val response = authenticatedUserToUserResponse(request.subject.user)
    Future.successful(SuccessfulResponse(response))
  }
}
