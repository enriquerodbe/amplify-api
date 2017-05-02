package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.dtos.User._
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.Future

// scalastyle:off public.methods.have.type
class UserCrudController @Inject()(
    val actionBuilder: ActionBuilders) extends Controller with AuthenticatedRequests {

  def fetchUser() = authenticated() { request ⇒
    val response = authenticatedUserToUserResponse(request.subject.user)
    Future.successful(Ok(Json.toJson(response)))
  }
}
