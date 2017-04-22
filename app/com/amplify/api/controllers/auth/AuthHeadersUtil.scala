package com.amplify.api.controllers.auth

import com.amplify.api.domain.models.ContentProviderType
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.exceptions.{MissingAuthTokenHeader, UnsupportedAuthProvider}
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import play.api.mvc.Request
import scala.concurrent.{ExecutionContext, Future}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  def getAuthData(request: Request[_]): Future[AuthData] = {
    val authProvider = request.headers.get("auth-provider") match {
      case Some(providerName) ⇒
        ContentProviderType.find(providerName) ?! UnsupportedAuthProvider(providerName)
      case _ ⇒
        Future.successful(ContentProviderType.default)
    }

    for {
      authProvider ← authProvider
      authToken ← request.headers.get("auth-token") ?! MissingAuthTokenHeader
    }
    yield AuthData(authProvider, authToken)
  }
}

case class AuthData(authProviderType: ContentProviderType, authToken: String)
