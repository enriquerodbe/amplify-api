package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthUser
import com.amplify.api.controllers.converters.JsonConverters._
import com.amplify.api.domain.logic.VenueCrudLogic
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.language.reflectiveCalls

// scalastyle:off public.methods.have.type
class VenueCrudController @Inject()(
    venueCrudLogic: VenueCrudLogic,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller {

  def fetchPlaylists(offset: Int, limit: Int) = {
    actionBuilder.SubjectPresentAction().defaultHandler() { request ⇒
      val authUser = request.subject.get.asInstanceOf[AuthUser]
      implicit val user = authUser.user
      venueCrudLogic.retrievePlaylists(authUser.authToken).map { playlists ⇒
        Ok(Json.toJson(playlists))
      }
    }
  }
}
