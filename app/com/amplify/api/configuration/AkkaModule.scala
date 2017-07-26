package com.amplify.api.configuration

import com.amplify.api.aggregates.queue.{CommandProcessor ⇒ QueueCommandProcessor, CommandRouter}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class AkkaModule extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[CommandRouter]("queue-command-router")
    bindActorFactory[QueueCommandProcessor, QueueCommandProcessor.Factory]
  }
}
