package com.amplify.api.services

import com.amplify.api.domain.models.{Coin, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinServiceImpl])
trait CoinService {

  def createCoins(venue: Venue, number: Int): Future[Seq[Coin]]
}
