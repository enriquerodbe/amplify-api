package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinToken
import com.amplify.api.domain.venue.DbVenue
import com.amplify.api.shared.configuration.EnvConfig
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class CoinDaoImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider, envConfig: EnvConfig)
  extends CoinDao with CoinsTable {

  import profile.api._

  lazy private val defaultMaxUsages = envConfig.coinsDefaultMaxUsages

  override def create(dbVenue: DbVenue, number: Int): DBIO[Seq[DbCoin]] = {
    val dbCoins =
      Seq.fill(number) {
        DbCoin(
          venueId = dbVenue.id,
          token = CoinToken.generate(dbVenue.uid),
          maxUsages = defaultMaxUsages)
      }

    insertCoinsQuery ++= dbCoins
  }

  override def retrieve(coinToken: CoinToken): DBIO[Option[DbCoin]] = {
    coinsTable.filter(_.token === coinToken).result.headOption
  }
}
