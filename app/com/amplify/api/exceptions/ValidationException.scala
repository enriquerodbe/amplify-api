package com.amplify.api.exceptions

trait ValidationException

case class InvalidProviderIdentifier(identifier: String)
  extends BadRequestException(
    AppExceptionCode.InvalidProviderIdentifier,
    s"Invalid content provider identifier: $identifier")
    with ValidationException

case class InvalidCoinToken(token: String)
  extends BadRequestException(
      AppExceptionCode.InvalidCoinToken,
      s"Invalid coin token: $token")
    with ValidationException

case class InvalidCreateCoinsRequestedNumber(max: Int, number: Int)
  extends BadRequestException(
    AppExceptionCode.InvalidCreateCoinsRequestedNumber,
    s"Number must be between 1 and $max. Was: $number")
    with ValidationException
