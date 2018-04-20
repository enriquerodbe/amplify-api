package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.DbCoin
import com.amplify.api.daos.schema.CoinsTable
import com.amplify.api.domain.models.primitives.Id

trait DbCoinFixture extends BaseDbFixture with CommonData with CoinsTable {

  import profile.api._

  def findCoins(venueId: Id): Seq[DbCoin] = {
    await(db.run(coinsTable.filter(_.venueId === venueId).result))
  }
}
