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
