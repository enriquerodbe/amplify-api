package com.amplify.api.shared.configuration

import com.amplify.api.domain.queue.{CommandRouter, PlaybackNotifier, CommandProcessor ⇒ QueueCommandProcessor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class AkkaModule extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[CommandRouter]("queue-command-router")
    bindActor[PlaybackNotifier]("queue-playback-notifier")
    bindActorFactory[QueueCommandProcessor, QueueCommandProcessor.Factory]
  }
}
