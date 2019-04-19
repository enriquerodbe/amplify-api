package com.amplify.api.domain.coin

import com.amplify.api.domain.models.{Coin, CoinCode, CoinStatus, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinServiceImpl])
trait CoinService {

  def createCoins(venue: Venue, number: Int): Future[Seq[Coin]]

  def login(coinCode: CoinCode): Future[Option[Coin]]

  def retrieveStatus(coin: Coin): Future[CoinStatus]
}
