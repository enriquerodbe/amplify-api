package com.amplify.api.domain.coin

import com.amplify.api.domain.models.{Coin, CoinStatus, CoinToken, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinLogicImpl])
trait CoinLogic {

  def createCoins(venue: Venue, number: Int): Future[Seq[Coin]]

  def login(coinToken: CoinToken): Future[Option[Coin]]

  def retrieveStatus(coin: Coin): Future[CoinStatus]
}
