package com.amplify.api.exceptions

import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode

abstract class AppException(code: AppExceptionCode, message: String) extends Exception(message)

object AppExceptionCode extends Enumeration {

  type AppExceptionCode = Value

  val
    UnsupportedAuthProvider,
    UserAuthTokenNotFound
  = Value
}
