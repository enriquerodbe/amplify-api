package com.amplify.api.domain.logic

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.{Coin, Venue}
import com.amplify.api.exceptions.InvalidCreateCoinsRequestedNumber
import com.amplify.api.services.CoinService
import javax.inject.Inject
import scala.concurrent.Future

class CoinLogicImpl @Inject()(envConfig: EnvConfig, coinService: CoinService) extends CoinLogic {

  val maxCreatePerRequest = envConfig.coinsCreateMax

  override def createCoins(venue: Venue, number: Int): Future[Seq[Coin]] = {
    if (number <= 0 || number > maxCreatePerRequest) {
      Future.failed(InvalidCreateCoinsRequestedNumber(maxCreatePerRequest, number))
    }
    else coinService.createCoins(venue, number)
  }
}
