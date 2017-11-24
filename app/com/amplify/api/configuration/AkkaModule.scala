package com.amplify.api.configuration

import com.amplify.api.aggregates.queue.{CommandRouter, PushNotificationGateway, CommandProcessor ⇒ QueueCommandProcessor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class AkkaModule extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[PushNotificationGateway]("push-notification-gateway")
    bindActor[CommandRouter]("queue-command-router")
    bindActorFactory[QueueCommandProcessor, QueueCommandProcessor.Factory]
  }
}
