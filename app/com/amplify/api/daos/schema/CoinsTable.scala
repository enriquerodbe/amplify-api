package com.amplify.api.daos.schema

import com.amplify.api.daos.models.DbCoin
import com.amplify.api.domain.models.CoinToken
import com.amplify.api.domain.models.primitives.Id

trait CoinsTable extends BaseTable {

  import profile.api._

  implicit val coinTokenType = {
    MappedColumnType.base[CoinToken, String](_.toString, CoinToken.fromString(_).get)
  }

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Coins(tag: Tag) extends Table[DbCoin](tag, "coins") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def venueId = column[Id]("venue_id")
    def token = column[CoinToken]("token")
    def maxUsages = column[Int]("max_usages")

    def * = (id, venueId, token, maxUsages) <> (DbCoin.tupled, DbCoin.unapply)
  }

  lazy val coinsTable = TableQuery[Coins]
  lazy val insertCoinsQuery =
    coinsTable.returning(coinsTable.map(_.id)).into((obj, id) ⇒ obj.copy(id = id))
}
