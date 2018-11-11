package com.amplify.api.domain.coin

import com.amplify.api.domain.models.{Coin, CoinToken, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinServiceImpl])
trait CoinService {

  def retrieve(coinToken: CoinToken): Future[Option[Coin]]

  def create(venue: Venue, number: Int): Future[Seq[Coin]]
}
