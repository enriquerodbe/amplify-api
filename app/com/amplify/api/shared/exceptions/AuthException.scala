package com.amplify.api.shared.exceptions

sealed trait AuthException

case object UserAuthTokenNotFound
  extends UnauthorizedException(
    AppExceptionCode.UserAuthTokenNotFound,
    "User authentication token not found")
    with AuthException

case object MissingCoin
  extends BadRequestException(AppExceptionCode.MissingCoin, "Missing coin query param")
    with AuthException
