package com.amplify.api.controllers.auth

import com.amplify.api.domain.models.{AuthToken, ContentProviderType}
import com.amplify.api.exceptions.{MissingAuthTokenHeader, UnsupportedAuthProvider}
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import play.api.mvc.{Headers, Request}
import scala.concurrent.ExecutionContext
import scala.util.{Success, Try}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  val AUTH_PROVIDER_HEADER = "auth-provider"
  val AUTH_TOKEN_HEADER = "auth-token"

  def getAuthToken(request: Request[_]): Try[AuthToken] = {
    val headers = request.headers
    for {
      authProvider ← getContentProvider(headers)
      authToken ← headers.get(AUTH_TOKEN_HEADER) ?? MissingAuthTokenHeader
    }
    yield AuthToken(authProvider, authToken)
  }

  private def getContentProvider(headers: Headers) = {
    headers.get(AUTH_PROVIDER_HEADER) match {
      case Some(providerName) ⇒
        ContentProviderType.find(providerName) ?? UnsupportedAuthProvider(providerName)
      case _ ⇒
        Success(ContentProviderType.default)
    }
  }
}
