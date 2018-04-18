package com.amplify.api.daos

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.daos.models.{DbCoin, DbVenue}
import com.amplify.api.daos.schema.CoinsTable
import com.amplify.api.domain.models.CoinToken
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class CoinDaoImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider, envConfig: EnvConfig)
  extends CoinDao with CoinsTable {

  import profile.api._

  lazy private val defaultMaxUsages = envConfig.coinsDefaultMaxUsages

  override def createCoins(dbVenue: DbVenue, number: Int): DBIO[Seq[DbCoin]] = {
    val dbCoins =
      Seq.fill(number) {
        DbCoin(
          venueId = dbVenue.id,
          token = CoinToken.generate(dbVenue.uid),
          maxUsages = defaultMaxUsages)
      }

    insertCoinsQuery ++= dbCoins
  }
}
