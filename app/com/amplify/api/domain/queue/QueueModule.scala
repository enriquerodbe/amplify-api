package com.amplify.api.domain.queue

import com.amplify.api.domain.queue.{CommandProcessor ⇒ QueueCommandProcessor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class QueueModule extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[CommandRouter]("queue-command-router")
    bindActor[PlaybackNotifier]("queue-playback-notifier")
    bindActorFactory[QueueCommandProcessor, QueueCommandProcessor.Factory]
  }
}
