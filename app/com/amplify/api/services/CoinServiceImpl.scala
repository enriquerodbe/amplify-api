package com.amplify.api.services

import com.amplify.api.daos.{CoinDao, DbioRunner, VenueDao}
import com.amplify.api.domain.models.{Coin, Venue}
import com.amplify.api.exceptions.VenueNotFoundByUid
import com.amplify.api.services.converters.CoinConverter.dbCoinToCoin
import com.amplify.api.utils.DbioUtils.DbioT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CoinServiceImpl @Inject()(
    db: DbioRunner,
    coinDao: CoinDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends CoinService {

  override def createCoins(venue: Venue, number: Int): Future[Seq[Coin]] = {
    val actions =
      for {
        dbVenue ← venueDao.retrieve(venue.uid) ?! VenueNotFoundByUid(venue.uid)
        coins ← coinDao.createCoins(dbVenue, number).map(_.map(dbCoinToCoin(_, Seq.empty)))
      }
      yield coins

    db.run(actions)
  }
}
