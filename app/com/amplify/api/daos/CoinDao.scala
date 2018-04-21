package com.amplify.api.daos

import com.amplify.api.daos.models.{DbCoin, DbVenue}
import com.amplify.api.domain.models.CoinToken
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinDaoImpl])
trait CoinDao {

  def create(dbVenue: DbVenue, number: Int): DBIO[Seq[DbCoin]]

  def retrieve(coinToken: CoinToken): DBIO[Option[DbCoin]]
}
