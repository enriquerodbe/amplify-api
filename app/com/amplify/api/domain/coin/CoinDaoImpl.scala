package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Coin
import com.amplify.api.domain.models.primitives.{Code, Id, Uid}
import com.amplify.api.shared.configuration.EnvConfig
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

private class CoinDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    envConfig: EnvConfig)(
    implicit ec: ExecutionContext)
    extends CoinDao with CoinsTable {

  import profile.api._

  lazy private val defaultMaxUsages = envConfig.coinsDefaultMaxUsages

  override def createCoins(venueUid: Uid, number: Int): DBIO[Seq[DbCoin]] = {
    val dbCoins =
      Seq.fill(number) {
        DbCoin(
          id = Id(0),
          venueUid = venueUid,
          code = Code.generate(),
          maxUsages = defaultMaxUsages)
      }

    insertCoinsQuery ++= dbCoins
  }

  override def retrieve(code: Code): DBIO[Seq[DbCoin]] = coinsTable.filter(_.code === code).result

  override def retrieve(coin: Coin): DBIO[Option[DbCoin]] = {
    coinsTable
      .filter(row ⇒ row.venueUid === coin.venueUid && row.code === coin.code)
      .result
      .headOption
  }
}
