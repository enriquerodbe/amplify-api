package com.amplify.api.controllers.auth

import com.amplify.api.domain.models.{AuthToken, AuthProviderType}
import com.amplify.api.exceptions.{MissingAuthTokenHeader, UnsupportedAuthProvider, WrongAuthorizationHeader}
import com.amplify.api.utils.DbioUtils.OptionT
import javax.inject.Inject
import play.api.mvc.{Headers, Request}
import play.mvc.Http
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  val AUTH_PROVIDER_HEADER = "Auth-provider"

  def getAuthToken(request: Request[_]): Try[AuthToken] = {
    val headers = request.headers
    for {
      authProvider ← getAuthProvider(headers)
      authorizationHeader ← headers.get(Http.HeaderNames.AUTHORIZATION) ?! MissingAuthTokenHeader
      authToken ← parseAuthToken(authorizationHeader)
    }
    yield AuthToken(authProvider, authToken)
  }

  private def getAuthProvider(headers: Headers) = {
    headers.get(AUTH_PROVIDER_HEADER) match {
      case Some(providerName) ⇒
        AuthProviderType.find(providerName) ?! UnsupportedAuthProvider(providerName)
      case _ ⇒
        Success(AuthProviderType.default)
    }
  }

  private def parseAuthToken(authorizationHeader: String) = {
    val regex = """Bearer (.*)""".r
    authorizationHeader match {
      case regex(authToken) ⇒ Success(authToken)
      case _ ⇒ Failure(WrongAuthorizationHeader(authorizationHeader))
    }
  }
}
