package com.amplify.api.domain.coin

import com.amplify.api.domain.coin.CoinConverter.dbCoinToCoin
import com.amplify.api.domain.models.{Coin, CoinCode, CoinStatus, Venue}
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.daos.DbioRunner
import com.amplify.api.shared.exceptions.{InvalidCreateCoinsRequestedNumber, VenueNotFoundByUid}
import com.amplify.api.utils.FutureUtils._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CoinServiceImpl @Inject()(
    db: DbioRunner,
    envConfig: EnvConfig,
    venueService: VenueService,
    queueService: QueueService,
    coinDao: CoinDao)(
    implicit ec: ExecutionContext) extends CoinService {

  val maxCreatePerRequest = envConfig.coinsCreateMax
  implicit val defaultTimeout = envConfig.defaultAskTimeout

  override def createCoins(venue: Venue, number: Int): Future[Seq[Coin]] = {
    if (number <= 0 || number > maxCreatePerRequest) {
      Future.failed(InvalidCreateCoinsRequestedNumber(maxCreatePerRequest, number))
    }
    else create(venue, number)
  }

  private def create(venue: Venue, number: Int): Future[Seq[Coin]] = {
    db.run(coinDao.create(venue.uid, number).map(_.map(dbCoinToCoin(_, Seq.empty))))
  }

  override def login(coinCode: CoinCode): Future[Option[Coin]] = {
    db.run(coinDao.retrieve(coinCode).map(_.map(dbCoinToCoin(_, Seq.empty))))
  }

  override def retrieveStatus(coin: Coin): Future[CoinStatus] = {
    val venueUid = coin.code.venueUid
    for {
      venue ← venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
      queue ← queueService.retrieveQueue(venue)
    }
    yield CoinStatus(coin, venue, queue.currentItem)
  }
}
