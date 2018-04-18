package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{Coin, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinLogicImpl])
trait CoinLogic {

  def createCoins(venue: Venue, number: Int): Future[Seq[Coin]]
}
