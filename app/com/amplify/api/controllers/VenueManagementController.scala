package com.amplify.api.controllers

import com.amplify.api.controllers.VenueManagementRequestParams.SignUp
import com.amplify.api.domain.logic.{VenueCrudLogic, VenueSignUpLogic}
import com.amplify.api.domain.models.AuthProviderType
import com.amplify.api.exceptions.UnsupportedAuthProvider
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext

class VenueManagementController @Inject()(
    venueSignUpLogic: VenueSignUpLogic,
    venueCrudLogic: VenueCrudLogic)(
    implicit ec: ExecutionContext) extends Controller {

  def signUp(authProvider: String, authToken: String): Action[SignUp] = {
    Action.async(parse.json[SignUp]) { request ⇒
      for {
        providerType ← AuthProviderType.find(authProvider) ?! UnsupportedAuthProvider(authProvider)
        _ ← venueSignUpLogic.signUp(request.body.name, providerType, authToken)
      }
      yield Created
    }
  }

  def list: Action[AnyContent] = Action.async {
    for (venues ← venueCrudLogic.listAll) yield Ok(venues.toString)
  }
}

object VenueManagementRequestParams {

  case class SignUp(name: String)
  implicit val signUpFormat: Format[SignUp] = Json.format[SignUp]
}
