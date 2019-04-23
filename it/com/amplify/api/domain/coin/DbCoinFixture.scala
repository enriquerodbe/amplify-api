package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.it.fixtures.{BaseDbFixture, CommonData}

trait DbCoinFixture extends BaseDbFixture with CommonData with CoinsTable {

  import profile.api._

  val validDbCoin = DbCoin(aliceVenueUid, Code(validCoinCodeStr), 1)

  def insertCoin(dbCoin: DbCoin): DbCoin = await(db.run(insertCoinsQuery += dbCoin))

  def findCoins(venueUid: Uid): Seq[DbCoin] = {
    await(db.run(coinsTable.filter(_.venueUid === venueUid).result))
  }

  insertCoin(validDbCoin)
}
