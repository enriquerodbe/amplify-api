package com.amplify.api.controllers

import com.amplify.api.controllers.VenueManagementRequestParams.SignUp
import com.amplify.api.domain.logic.{VenueAuthLogic, VenueCrudLogic}
import com.amplify.api.domain.models.AuthProviderType
import com.amplify.api.exceptions.UnsupportedAuthProvider
import com.amplify.api.utils.FutureUtils.OptionT
import com.github.tototoshi.play.json.JsonNaming
import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class VenueManagementController @Inject()(
    venueAuthLogic: VenueAuthLogic,
    venueCrudLogic: VenueCrudLogic)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp = {
    Action.async(parse.json[SignUp]) { request ⇒
      val authProvider = request.body.authProvider
      val authToken = request.body.authToken
      val venueName = request.body.name

      for {
        providerType ← AuthProviderType.find(authProvider) ?! UnsupportedAuthProvider(authProvider)
        _ ← venueAuthLogic.signUp(venueName, providerType, authToken)
      }
      yield Created
    }
  }

  def login(authProvider: String, authToken: String) = Action.async {
    for {
      providerType ← AuthProviderType.find(authProvider) ?! UnsupportedAuthProvider(authProvider)
      _ ← venueAuthLogic.login(providerType, authToken)
    }
    yield Ok
  }

  def list = Action.async {
    for (venues ← venueCrudLogic.listAll) yield Ok(venues.toString)
  }
}

object VenueManagementRequestParams {

  case class SignUp(name: String, authProvider: String, authToken: String)
  implicit val signUpFormat: Format[SignUp] = JsonNaming.snakecase(Json.format[SignUp])
}
