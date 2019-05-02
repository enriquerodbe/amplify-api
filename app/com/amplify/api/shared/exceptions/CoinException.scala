package com.amplify.api.shared.exceptions

import com.amplify.api.domain.models.Coin

trait CoinException

case class CoinMaxUsages(coin: Coin)
  extends BadRequestException(
      AppExceptionCode.CoinMaxUsages,
      s"Coin code ${coin.code} for venue ${coin.venueUid} reached " +
      s"the maximum number of usages of ${coin.maxUsages}")
      with CoinException

case class CoinNotFound(coin: Coin)
  extends InternalException(
      AppExceptionCode.CoinNotFound,
      s"Coin code ${coin.code} not found for venue ${coin.venueUid}")
      with CoinException
