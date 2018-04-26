package com.amplify.api.domain.logic

import akka.actor.ActorRef
import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandRouter.RetrieveQueue
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.{InvalidCreateCoinsRequestedNumber, VenueNotFoundByUid}
import com.amplify.api.services.{CoinService, VenueService}
import com.amplify.api.utils.FutureUtils._
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class CoinLogicImpl @Inject()(
    envConfig: EnvConfig,
    coinService: CoinService,
    venueService: VenueService,
    @Named("queue-command-router") queueCommandRouter: ActorRef)(
    implicit ec: ExecutionContext) extends CoinLogic {

  val maxCreatePerRequest = envConfig.coinsCreateMax
  implicit val defaultTimeout = envConfig.defaultAskTimeout

  override def createCoins(venue: Venue, number: Int): Future[Seq[Coin]] = {
    if (number <= 0 || number > maxCreatePerRequest) {
      Future.failed(InvalidCreateCoinsRequestedNumber(maxCreatePerRequest, number))
    }
    else coinService.create(venue, number)
  }

  override def login(coinToken: CoinToken): Future[Option[Coin]] = coinService.retrieve(coinToken)

  override def retrieveStatus(coin: Coin): Future[CoinStatus] = {
    val venueUid = coin.token.venueUid
    for {
      venue ← venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
      queue ← (queueCommandRouter ? RetrieveQueue(venue)).mapTo[Queue]
    }
    yield CoinStatus(coin, venue, queue.currentItem)
  }
}
