package com.amplify.api.configuration

import com.amplify.api.command_processors.queue.{CommandProcessor ⇒ QueueCommandProcessor, CommandRouter ⇒ QueueCommandRouter}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class AkkaModule extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[QueueCommandRouter]("queue-command-router")
    bindActorFactory[QueueCommandProcessor, QueueCommandProcessor.Factory]
  }
}
