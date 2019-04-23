package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Coin, CoinCode, CoinStatus}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinServiceImpl])
trait CoinService {

  def createCoins(venueUid: Uid, number: Int): Future[Seq[Coin]]

  def login(coinCode: CoinCode): Future[Option[Coin]]

  def retrieveStatus(coin: Coin): Future[CoinStatus]
}
