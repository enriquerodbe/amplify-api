package com.amplify.api.shared.exceptions

import com.amplify.api.domain.models.primitives.Code

trait ValidationException

case class InvalidProviderIdentifier(identifier: String)
  extends BadRequestException(
    AppExceptionCode.InvalidProviderIdentifier,
    s"Invalid content provider identifier: $identifier")
    with ValidationException

case class InvalidCoinCode(coinCode: String)
  extends BadRequestException(
      AppExceptionCode.InvalidCoinCode,
      s"Invalid coin code: $coinCode")
    with ValidationException

case class InvalidCreateCoinsRequestedNumber(max: Int, number: Int)
  extends BadRequestException(
    AppExceptionCode.InvalidCreateCoinsRequestedNumber,
    s"Number must be between 1 and $max. Was: $number")
    with ValidationException

case class CodeMatchesMultipleCoins(_code: Code)
  extends InternalException(
    AppExceptionCode.CodeMatchesMultipleCoins,
    s"Code ${_code} matches multiple coins from different venues")
    with ValidationException
