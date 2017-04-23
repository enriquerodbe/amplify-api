package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.converters.JsonConverters._
import com.amplify.api.domain.logic.VenueCrudLogic
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.language.reflectiveCalls

// scalastyle:off public.methods.have.type
class VenueCrudController @Inject()(
    venueCrudLogic: VenueCrudLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def fetchPlaylists(offset: Int, limit: Int) = authenticated() { request ⇒
    val authUser = request.authUser
    val eventualPlaylists = venueCrudLogic.retrievePlaylists(authUser.user, authUser.authToken)
    eventualPlaylists.map(playlists ⇒ Ok(Json.toJson(playlists)))
  }
}
