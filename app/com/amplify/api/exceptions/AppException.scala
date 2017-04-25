package com.amplify.api.exceptions

import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode

abstract class AppException(code: AppExceptionCode, message: String) extends Exception(message)

abstract class InternalException(code: AppExceptionCode, message: String)
  extends AppException(code, message)

abstract class BadRequestException(code: AppExceptionCode, message: String)
  extends AppException(code, message)

object AppExceptionCode extends Enumeration {

  type AppExceptionCode = Value

  val
    UnsupportedAuthProvider,
    UserAuthTokenNotFound,
    UserNotFound,
    SpotifyError,
    MissingAuthProviderHeader,
    MissingAuthTokenHeader,
    UnexpectedExternalServiceResponse
  = Value
}
