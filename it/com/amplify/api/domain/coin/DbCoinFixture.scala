package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.{Code, Id, Uid}
import com.amplify.api.it.fixtures.{BaseDbFixture, CommonData}

trait DbCoinFixture extends BaseDbFixture with CommonData with CoinsTable with CoinUsagesTable {

  import profile.api._

  val unusedCoin = DbCoin(Id(1), aliceVenueUid, Code(unusedCoinCode), 1)
  val usedCoin = DbCoin(Id(2), aliceVenueUid, Code(usedCoinCode), 1)
  val coinUsage = CoinUsage(usedCoin.id, 1)

  def insertCoin(dbCoin: DbCoin): DbCoin = await(db.run(insertCoinsQuery += dbCoin))
  def insertCoinUsage(coinUsage: CoinUsage): Int = await(db.run(coinUsagesTable += coinUsage))

  def findCoins(venueUid: Uid): Seq[DbCoin] = {
    await(db.run(coinsTable.filter(_.venueUid === venueUid).result))
  }
  def findCoinUsages(coinId: Id): Seq[CoinUsage] = {
    await(db.run(coinUsagesTable.filter(_.coinId === coinId).result))
  }

  insertCoin(unusedCoin)
  insertCoin(usedCoin)
  insertCoinUsage(coinUsage)
}
