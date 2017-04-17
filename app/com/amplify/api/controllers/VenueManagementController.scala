package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.VenueManagementRequestParams.SignUp
import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.logic.{UserAuthLogic, VenueAuthLogic, VenueCrudLogic}
import com.amplify.api.domain.models.AuthProviderType
import com.github.tototoshi.play.json.JsonNaming
import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.language.reflectiveCalls

// scalastyle:off public.methods.have.type
class VenueManagementController @Inject()(
    venueAuthLogic: VenueAuthLogic,
    venueCrudLogic: VenueCrudLogic,
    authHeadersUtil: AuthHeadersUtil,
    actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp = {
    actionBuilder.SubjectNotPresentAction().defaultHandler()(parse.json[SignUp]) { request ⇒
      for {
        authData ← authHeadersUtil.getAuthData(request)
        _ ← venueAuthLogic.signUp(authData.authProviderType, authData.authToken, request.body.name)
      }
      yield Created
    }
  }

  def list = actionBuilder.SubjectPresentAction().defaultHandler() {
    for (venues ← venueCrudLogic.listAll) yield Ok(venues.toString)
  }
}

object VenueManagementRequestParams {

  case class SignUp(name: String)
  implicit val signUpFormat: Format[SignUp] = JsonNaming.snakecase(Json.format[SignUp])
}
