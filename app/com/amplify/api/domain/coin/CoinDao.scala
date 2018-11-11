package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinToken
import com.amplify.api.domain.venue.DbVenue
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinDaoImpl])
trait CoinDao {

  def create(dbVenue: DbVenue, number: Int): DBIO[Seq[DbCoin]]

  def retrieve(coinToken: CoinToken): DBIO[Option[DbCoin]]
}
