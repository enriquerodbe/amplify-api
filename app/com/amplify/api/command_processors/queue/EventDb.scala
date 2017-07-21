package com.amplify.api.command_processors.queue

import com.amplify.api.command_processors.queue.CommandProcessor.Command
import com.amplify.api.command_processors.queue.EventType.QueueEventType
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models._
import java.time.Instant

case class EventDb(
    id: Id[Event] = -1L,
    queueCommandId: Id[Command],
    queueEventType: QueueEventType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
