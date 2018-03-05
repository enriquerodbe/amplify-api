package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.amplify.api.domain.logic.{UserAuthLogic, VenueAuthLogic}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmplifyHandlerCache @Inject()(
    userAuthLogic: UserAuthLogic,
    venueAuthLogic: VenueAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends HandlerCache {

  private val userHandler = new UserDeadboltHandler(userAuthLogic, authHeadersUtil)
  private val venueHandler = new VenueDeadboltHandler(venueAuthLogic, authHeadersUtil)
  private val defaultHandler = new EmptyHandler(authHeadersUtil)

  private val handlers: Map[HandlerKey, DeadboltHandler] =
    Map(HandlerKeys.UserHandlerKey → userHandler, HandlerKeys.VenueHandlerKey → venueHandler)

  override def apply(handlerKey: HandlerKey): DeadboltHandler = {
    handlers.getOrElse(handlerKey, defaultHandler)
  }

  override def apply(): DeadboltHandler = defaultHandler
}

object HandlerKeys {

  sealed trait AmplifyHandlerKey extends HandlerKey
  case object UserHandlerKey extends AmplifyHandlerKey
  case object VenueHandlerKey extends AmplifyHandlerKey
}
