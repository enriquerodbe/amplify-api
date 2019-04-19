package com.amplify.api.it.fixtures

import com.amplify.api.domain.coin.{CoinsTable, DbCoin}
import com.amplify.api.domain.models.primitives.{Code, Id, Uid}

trait DbCoinFixture extends BaseDbFixture with CommonData with CoinsTable {

  import profile.api._

  val validDbCoin = DbCoin(Id(validDbCoinId), aliceVenueUid, Code(validCoinCodeStr), 1)

  def insertCoin(dbCoin: DbCoin): DbCoin = await(db.run(insertCoinsQuery += dbCoin))

  def findCoins(venueUid: Uid): Seq[DbCoin] = {
    await(db.run(coinsTable.filter(_.venueUid === venueUid).result))
  }

  insertCoin(validDbCoin)
}
