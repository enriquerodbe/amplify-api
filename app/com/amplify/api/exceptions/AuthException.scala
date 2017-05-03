package com.amplify.api.exceptions

sealed trait AuthException

case object UserAuthTokenNotFound
  extends ForbiddenException(
    AppExceptionCode.UserAuthTokenNotFound,
    s"User authentication token not found")
    with AuthException

case class UnsupportedAuthProvider(authProviderName: String)
  extends BadRequestException(
    AppExceptionCode.UnsupportedAuthProvider,
    s"Unsupported authentication provider: $authProviderName")
    with AuthException

case object MissingAuthTokenHeader
  extends BadRequestException(AppExceptionCode.MissingAuthTokenHeader, s"Missing auth-token header")
    with AuthException
