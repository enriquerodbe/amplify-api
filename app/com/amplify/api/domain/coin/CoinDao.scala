package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Coin
import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinDaoImpl])
private trait CoinDao {

  def createCoins(venueUid: Uid, number: Int): DBIO[Seq[DbCoin]]

  def retrieve(code: Code): DBIO[Seq[DbCoin]]

  def retrieve(coin: Coin): DBIO[Option[DbCoin]]
}
