package com.amplify.api.services

import com.amplify.api.daos.{CoinDao, DbioRunner, VenueDao}
import com.amplify.api.domain.models.{Coin, CoinToken, Venue}
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

  override def create(venue: Venue, number: Int): Future[Seq[Coin]] = {
    val actions =
      for {
        dbVenue ← venueDao.retrieve(venue.uid) ?! VenueNotFoundByUid(venue.uid)
        coins ← coinDao.create(dbVenue, number).map(_.map(dbCoinToCoin(_, Seq.empty)))
      }
      yield coins

    db.run(actions)
  }

  override def retrieve(coinToken: CoinToken): Future[Option[Coin]] = {
    db.run(coinDao.retrieve(coinToken).map(_.map(dbCoinToCoin(_, Seq.empty))))
  }
}
