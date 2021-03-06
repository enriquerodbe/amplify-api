package com.amplify.api.shared.exceptions

import com.amplify.api.shared.exceptions.AppExceptionCode.AppExceptionCode

abstract class AppException(val code: AppExceptionCode, val message: String)
  extends Exception(message)

abstract class InternalException(override val code: AppExceptionCode, override val message: String)
  extends AppException(code, message)

abstract class BadRequestException(
    override val code: AppExceptionCode,
    override val message: String)
  extends AppException(code, message)

abstract class UnauthorizedException(
    override val code: AppExceptionCode,
    override val message: String)
  extends BadRequestException(code, message)

object AppExceptionCode extends Enumeration {

  type AppExceptionCode = Value

  val
    Unexpected,
    BadRequest,
    UserAuthTokenNotFound,
    WrongAuthorizationHeader,
    UserNotFound,
    SpotifyError,
    UnexpectedExternalServiceResponse,
    ExternalResourceNotFound,
    InvalidProviderIdentifier,
    VenueNotFound,
    AuthenticationFailed,
    CoinNotFound,
    InvalidCreateCoinsRequestedNumber,
    MissingCoin,
    CodeMatchesMultipleCoins,
    CoinMaxUsages
  = Value
}
