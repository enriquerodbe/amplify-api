package com.amplify.api.exceptions

sealed trait AuthException

case object UserAuthTokenNotFound
  extends UnauthorizedException(
    AppExceptionCode.UserAuthTokenNotFound,
    "User authentication token not found")
    with AuthException

case class WrongAuthorizationHeader(header: String)
  extends UnauthorizedException(
      AppExceptionCode.WrongAuthorizationHeader,
      s"Wrong authorization header: $header")

case class UnsupportedAuthProvider(authProviderName: String)
  extends BadRequestException(
    AppExceptionCode.UnsupportedAuthProvider,
    s"Unsupported authentication provider: $authProviderName")
    with AuthException

case object MissingAuthTokenHeader
  extends BadRequestException(AppExceptionCode.MissingAuthTokenHeader, "Missing auth-token header")
    with AuthException

case object MissingCoin
  extends BadRequestException(AppExceptionCode.MissingCoin, "Missing coin query param")
    with AuthException
