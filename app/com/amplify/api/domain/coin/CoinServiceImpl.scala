package com.amplify.api.domain.coin

import com.amplify.api.domain.coin.CoinConverter.dbCoinToCoin
import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.models.{Coin, TrackIdentifier}
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.daos.DbioRunner
import com.amplify.api.shared.exceptions.{CodeMatchesMultipleCoins, CoinMaxUsages, CoinNotFound, InvalidCreateCoinsRequestedNumber}
import com.amplify.api.utils.DbioUtils._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO

private class CoinServiceImpl @Inject()(
    db: DbioRunner,
    envConfig: EnvConfig,
    venueService: VenueService,
    queueService: QueueService,
    coinDao: CoinDao,
    coinUsageDao: CoinUsageDao)(
    implicit ec: ExecutionContext) extends CoinService {

  val maxCreatePerRequest = envConfig.coinsCreateMax
  implicit val defaultTimeout = envConfig.defaultAskTimeout

  override def createCoins(venueUid: Uid, number: Int): Future[Seq[Coin]] = {
    if (number <= 0 || number > maxCreatePerRequest) {
      Future.failed(InvalidCreateCoinsRequestedNumber(maxCreatePerRequest, number))
    }
    else create(venueUid, number)
  }

  private def create(venueUid: Uid, number: Int): Future[Seq[Coin]] = {
    db.run(coinDao.createCoins(venueUid, number).map(_.map(dbCoinToCoin)))
  }

  override def login(code: Code): Future[Option[Coin]] = {
    db.run(coinDao.retrieve(code)).flatMap {
      case coins if coins.size > 1 ⇒ Future.failed(CodeMatchesMultipleCoins(code))
      case coins ⇒ Future.successful(coins.headOption.map(dbCoinToCoin))
    }
  }

  override def addTrack(coin: Coin, trackIdentifier: TrackIdentifier): Future[Unit] = {
    for {
      () ← createCoinUsage(coin)
      () ← queueService.addTrack(coin.venueUid, coin.code, trackIdentifier)
    }
    yield ()
  }

  private def createCoinUsage(coin: Coin): Future[Unit] = db.run {
    for {
      dbCoin ← coinDao.retrieve(coin) ?! CoinNotFound(coin)
      maybeLastUsage ← coinUsageDao.retrieveLast(dbCoin)
      () ← validateMaxUsages(coin, maybeLastUsage)
      () ← coinUsageDao.create(dbCoin, maybeLastUsage)
    }
    yield ()
  }

  private def validateMaxUsages(coin: Coin, lastUsage: Option[CoinUsage]) = {
    if (lastUsage.exists(_.usageNumber >= coin.maxUsages)) {
      DBIO.failed(CoinMaxUsages(coin))
    }
    else DBIO.successful(())
  }

  override def retrieveRemainingUsages(coin: Coin): Future[Int] = db.run {
    for {
      dbCoin ← coinDao.retrieve(coin) ?! CoinNotFound(coin)
      usages ← coinUsageDao.retrieveLast(dbCoin)
    } yield {
      val timesUsed = usages.map(_.usageNumber).getOrElse(0)
      coin.maxUsages - timesUsed
    }
  }
}
