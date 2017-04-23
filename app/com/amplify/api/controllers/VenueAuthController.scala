package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.controllers.dtos.VenueManagement.SignUp
import com.amplify.api.domain.logic.VenueAuthLogic
import javax.inject.Inject
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext
import scala.language.reflectiveCalls

// scalastyle:off public.methods.have.type
class VenueAuthController @Inject()(
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp = Action.async(parse.json[SignUp]) { request ⇒
    for {
      authData ← authHeadersUtil.getAuthData(request)
      _ ← venueAuthLogic.signUp(authData.authProviderType, authData.authToken, request.body.name)
    }
    yield Created
  }
}
