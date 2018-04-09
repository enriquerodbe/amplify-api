package com.amplify.api.controllers.auth

import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.exceptions.{MissingAuthTokenHeader, UnsupportedAuthProvider, WrongAuthorizationHeader}
import com.amplify.api.utils.DbioUtils.OptionT
import javax.inject.Inject
import play.api.mvc.{Headers, RequestHeader}
import play.mvc.Http
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  private val AUTH_PROVIDER_HEADER = "Auth-provider"
  private val AUTH_PROVIDER_QUERY_PARAM = "auth-provider"
  private val TOKEN_QUERY_PARAM = "token"

  def getAuthTokenFromHeaders(request: RequestHeader): Try[AuthToken] = {
    val headers = request.headers
    for {
      authProvider ← getAuthProviderFromHeaders(headers)
      authorizationHeader ← headers.get(Http.HeaderNames.AUTHORIZATION) ?! MissingAuthTokenHeader
      authToken ← parseAuthToken(authorizationHeader)
    }
    yield AuthToken(authProvider, authToken)
  }

  private def getAuthProviderFromHeaders(headers: Headers) = {
    getAuthProvider(headers.get(AUTH_PROVIDER_HEADER))
  }

  def getAuthTokenFromQueryString(request: RequestHeader): Try[AuthToken] = {
    for {
      authProvider ← getAuthProviderFromQueryString(request)
      token ← request.getQueryString(TOKEN_QUERY_PARAM) ?! MissingAuthTokenHeader
    }
      yield AuthToken(authProvider, token)
  }

  private def getAuthProviderFromQueryString(request: RequestHeader) = {
    getAuthProvider(request.getQueryString(AUTH_PROVIDER_QUERY_PARAM))
  }

  private def getAuthProvider(maybeProvider: Option[String]) = {
    maybeProvider match {
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
