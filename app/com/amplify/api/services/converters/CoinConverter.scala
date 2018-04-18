package com.amplify.api.services.converters

import com.amplify.api.daos.models.{DbCoin, DbCoinUsage}
import com.amplify.api.domain.models.Coin

object CoinConverter {

  def dbCoinToCoin(dbCoin: DbCoin, usages: Seq[DbCoinUsage]): Coin = {
    Coin(dbCoin.token, dbCoin.maxUsages - usages.size)
  }
}
