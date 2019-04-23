package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinCode
import com.amplify.api.domain.models.primitives.{Code, Id, Uid}
import com.amplify.api.shared.daos.BaseTable

trait CoinsTable extends BaseTable {

  import profile.api._

  implicit val coinCodeType = {
    MappedColumnType.base[CoinCode, String](_.toString, CoinCode.fromString(_).get)
  }

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Coins(tag: Tag) extends Table[DbCoin](tag, "coins") {
    def venueUid = column[Uid]("venue_uid")
    def code = column[Code]("code")
    def maxUsages = column[Int]("max_usages")

    def * = (venueUid, code, maxUsages) <> (DbCoin.tupled, DbCoin.unapply)
  }

  lazy val coinsTable = TableQuery[Coins]
  lazy val insertCoinsQuery = coinsTable.returning(coinsTable)
}
