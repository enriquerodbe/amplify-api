package com.amplify.api.exceptions

import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode

abstract class AppException(val code: AppExceptionCode, val message: String)
  extends Exception(message)

abstract class InternalException(override val code: AppExceptionCode, override val message: String)
  extends AppException(code, message)

abstract class BadRequestException(
    override val code: AppExceptionCode,
    override val message: String)
  extends AppException(code, message)

object AppExceptionCode extends Enumeration {

  type AppExceptionCode = Value

  val
    Unexpected,
    UnsupportedAuthProvider,
    UserAuthTokenNotFound,
    UserNotFound,
    SpotifyError,
    MissingAuthProviderHeader,
    MissingAuthTokenHeader,
    UnexpectedExternalServiceResponse
  = Value
}
