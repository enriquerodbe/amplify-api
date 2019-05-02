package com.amplify.api.domain.coin

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

private class CoinUsageDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext) extends CoinUsageDao with CoinUsagesTable {

  import profile.api._

  override def retrieveLast(dbCoin: DbCoin): DBIO[Option[CoinUsage]] = {
    coinUsagesTable
        .filter(_.coinId === dbCoin.id)
        .sortBy(_.usageNumber.desc)
        .take(1)
        .result
        .headOption
  }

  override def create(dbCoin: DbCoin, maybeLastUsage: Option[CoinUsage]): DBIO[Unit] = {
    val newUsageNumber = maybeLastUsage.map(_.usageNumber).getOrElse(0) + 1
    (coinUsagesTable += CoinUsage(dbCoin.id, newUsageNumber)).map(_ ⇒ ())
  }
}
