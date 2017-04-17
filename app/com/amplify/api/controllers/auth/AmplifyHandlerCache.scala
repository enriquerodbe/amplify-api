package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.amplify.api.domain.logic.UserAuthLogic
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmplifyHandlerCache @Inject()(
    userAuthLogic: UserAuthLogic,
    authHeadersUtil: AuthHeadersUtil)(
    implicit ec: ExecutionContext) extends HandlerCache {

  private val defaultHandler = new AmplifyDeadboltHandler(userAuthLogic, authHeadersUtil)

  override def apply(handlerKey: HandlerKey): DeadboltHandler = defaultHandler

  override def apply(): DeadboltHandler = defaultHandler
}
