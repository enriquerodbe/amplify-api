package com.amplify.api.daos

import com.amplify.api.daos.models.{DbCoin, DbVenue}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinDaoImpl])
trait CoinDao {

  def createCoins(dbVenue: DbVenue, number: Int): DBIO[Seq[DbCoin]]
}
