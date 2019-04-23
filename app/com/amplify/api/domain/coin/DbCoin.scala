package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinCode
import com.amplify.api.domain.models.primitives.{Code, Uid}

case class DbCoin(venueUid: Uid, code: Code, maxUsages: Int) {

  def coinCode: CoinCode = CoinCode(venueUid, code)
}
