package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import com.amplify.api.domain.logic.{CoinLogic, VenueAuthLogic}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmplifyHandlerCache @Inject()(
    coinLogic: CoinLogic,
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends HandlerCache {

  private val coinHandler = new CoinDeadboltHandler(coinLogic, authHeadersUtil)
  private val venueHandler = new VenueDeadboltHandler(venueAuthLogic)
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
