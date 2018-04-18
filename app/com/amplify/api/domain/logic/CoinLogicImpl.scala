package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{Coin, Venue}
import com.amplify.api.services.CoinService
import javax.inject.Inject
import scala.concurrent.Future

class CoinLogicImpl @Inject()(coinService: CoinService) extends CoinLogic {

  override def createCoins(venue: Venue, number: Int): Future[Seq[Coin]] = {
    coinService.createCoins(venue, number)
  }
}
