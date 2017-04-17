package com.amplify.api.exceptions

trait AuthException

case class UserAuthTokenNotFound(authToken: String)
  extends BadRequestException(
    AppExceptionCode.UserAuthTokenNotFound,
    s"User authentication token not found: $authToken")
    with AuthException

case class UnsupportedAuthProvider(authProviderName: String)
  extends BadRequestException(
    AppExceptionCode.UnsupportedAuthProvider,
    s"Unsupported authentication provider: $authProviderName")
    with AuthException

case object MissingAuthProviderHeader
  extends BadRequestException(AppExceptionCode.MissingAuthProviderHeader,
    s"Request did not include auth-provider header")
    with AuthException

case object MissingAuthTokenHeader
  extends BadRequestException(AppExceptionCode.MissingAuthTokenHeader,
    s"Request did not include auth-token header")
    with AuthException
