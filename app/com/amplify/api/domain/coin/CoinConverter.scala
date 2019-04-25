package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Coin

private object CoinConverter {

  def dbCoinToCoin(dbCoin: DbCoin, usages: Seq[DbCoinUsage]): Coin = {
    Coin(dbCoin.venueUid, dbCoin.code, dbCoin.maxUsages - usages.size)
  }
}
