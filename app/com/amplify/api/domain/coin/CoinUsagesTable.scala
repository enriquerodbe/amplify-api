package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.Id
import com.amplify.api.shared.daos.BaseTable

trait CoinUsagesTable extends BaseTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class CoinUsages(tag: Tag) extends Table[CoinUsage](tag, "coin_usages") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def coinId = column[Id]("coin_id")
    def usageNumber = column[Int]("usage_number")

    def * = (coinId, usageNumber) <> (CoinUsage.tupled, CoinUsage.unapply)
  }

  val coinUsagesTable = TableQuery[CoinUsages]
}
