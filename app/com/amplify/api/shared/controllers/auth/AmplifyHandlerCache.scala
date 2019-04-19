package com.amplify.api.shared.controllers.auth

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import com.amplify.api.domain.coin.{CoinDeadboltHandler, CoinService}
import com.amplify.api.domain.venue.auth.{VenueAuthService, VenueDeadboltHandler}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmplifyHandlerCache @Inject()(
    coinService: CoinService,
    venueAuthService: VenueAuthService)(
    implicit ec: ExecutionContext) extends HandlerCache {

  private val coinHandler = new CoinDeadboltHandler(coinService)
  private val venueHandler = new VenueDeadboltHandler(venueAuthService)
  private val handlers: Map[HandlerKey, DeadboltHandler] =
    Map(HandlerKeys.CoinHandlerKey → coinHandler, HandlerKeys.VenueHandlerKey → venueHandler)

  override def apply(handlerKey: HandlerKey): DeadboltHandler = {
    handlers.getOrElse(handlerKey, EmptyHandler)
  }

  override def apply(): DeadboltHandler = EmptyHandler
}

object HandlerKeys {

  sealed trait AmplifyHandlerKey extends HandlerKey
  case object CoinHandlerKey extends AmplifyHandlerKey
  case object VenueHandlerKey extends AmplifyHandlerKey
}
