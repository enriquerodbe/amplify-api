package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinCode
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinDaoImpl])
private[coin] trait CoinDao {

  def create(venueUid: Uid, number: Int): DBIO[Seq[DbCoin]]

  def retrieve(coinCode: CoinCode): DBIO[Option[DbCoin]]
}
