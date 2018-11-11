package com.amplify.api.it.fixtures

import com.amplify.api.domain.coin.{CoinsTable, DbCoin}
import com.amplify.api.domain.models.CoinToken
import com.amplify.api.domain.models.primitives.Id

trait DbCoinFixture extends BaseDbFixture with CommonData with CoinsTable {

  import profile.api._

  val validCoinToken = CoinToken(aliceVenueUid, validCoinTokenStr)
  val validDbCoin = DbCoin(Id(validDbCoinId), Id(aliceDbVenueId), validCoinToken, 1)

  def insertCoin(dbCoin: DbCoin): DbCoin = await(db.run(insertCoinsQuery += dbCoin))

  def findCoins(venueId: Id): Seq[DbCoin] = {
    await(db.run(coinsTable.filter(_.venueId === venueId).result))
  }

  insertCoin(validDbCoin)
}
