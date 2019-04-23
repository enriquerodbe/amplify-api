package com.amplify.api.domain.coin

import com.amplify.api.domain.models.{Coin, CoinCode}

private object CoinConverter {

  def dbCoinToCoin(dbCoin: DbCoin, usages: Seq[DbCoinUsage]): Coin = {
    Coin(CoinCode(dbCoin.venueUid, dbCoin.code), dbCoin.maxUsages - usages.size)
  }
}
