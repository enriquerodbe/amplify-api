package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinCode
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.shared.configuration.EnvConfig
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

private class CoinDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider, envConfig: EnvConfig)
  extends CoinDao with CoinsTable {

  import profile.api._

  lazy private val defaultMaxUsages = envConfig.coinsDefaultMaxUsages

  override def create(venueUid: Uid, number: Int): DBIO[Seq[DbCoin]] = {
    val dbCoins =
      Seq.fill(number) {
        DbCoin(
          venueUid = venueUid,
          code = CoinCode.generate(venueUid).code,
          maxUsages = defaultMaxUsages)
      }

    insertCoinsQuery ++= dbCoins
  }

  override def retrieve(coinCode: CoinCode): DBIO[Option[DbCoin]] = {
    coinsTable
        .filter(_.venueUid === coinCode.venueUid)
        .filter(_.code === coinCode.code)
        .result
        .headOption
  }
}
