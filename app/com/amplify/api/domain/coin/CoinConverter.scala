package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Coin

object CoinConverter {

  def dbCoinToCoin(dbCoin: DbCoin, usages: Seq[DbCoinUsage]): Coin = {
    Coin(dbCoin.token, dbCoin.maxUsages - usages.size)
  }
}
