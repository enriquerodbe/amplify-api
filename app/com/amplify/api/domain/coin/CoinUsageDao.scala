package com.amplify.api.domain.coin

import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CoinUsageDaoImpl])
private trait CoinUsageDao {

  def retrieveLast(dbCoin: DbCoin): DBIO[Option[CoinUsage]]

  def create(dbCoin: DbCoin, maybeLastUsage: Option[CoinUsage]): DBIO[Unit]
}
