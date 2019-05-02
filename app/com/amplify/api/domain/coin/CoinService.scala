package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.models.{Coin, TrackIdentifier}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CoinServiceImpl])
trait CoinService {

  def createCoins(venueUid: Uid, number: Int): Future[Seq[Coin]]

  def login(code: Code): Future[Option[Coin]]

  def addTrack(coin: Coin, trackIdentifier: TrackIdentifier): Future[Unit]

  def retrieveRemainingUsages(coin: Coin): Future[Int]
}
