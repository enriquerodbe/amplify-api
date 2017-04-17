package com.amplify.api.controllers.auth

import com.amplify.api.domain.models.AuthProviderType
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.exceptions.{MissingAuthProviderHeader, MissingAuthTokenHeader, UnsupportedAuthProvider}
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import play.api.mvc.Request
import scala.concurrent.{ExecutionContext, Future}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  def getAuthData(request: Request[_]): Future[AuthData] = {
    for {
      providerName ← request.headers.get("auth-provider") ?! MissingAuthProviderHeader
      authProvider ← AuthProviderType.find(providerName) ?! UnsupportedAuthProvider(providerName)
      authToken ← request.headers.get("auth-token") ?! MissingAuthTokenHeader
    }
    yield AuthData(authProvider, authToken)
  }
}

case class AuthData(authProviderType: AuthProviderType, authToken: String)
