package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinCode
import com.amplify.api.domain.models.primitives.{Code, Id, Uid}

case class DbCoin(id: Id = Id(0L), venueUid: Uid, code: Code, maxUsages: Int) {

  def coinCode: CoinCode = CoinCode(venueUid, code)
}
