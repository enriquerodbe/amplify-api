package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Coin

private object CoinConverter {

  def dbCoinToCoin(dbCoin: DbCoin): Coin = Coin(dbCoin.venueUid, dbCoin.code, dbCoin.maxUsages)
}
