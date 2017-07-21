package com.amplify.api.command_processors.queue

import com.amplify.api.command_processors.queue.CommandProcessor.Command
import com.amplify.api.command_processors.queue.CommandType.QueueCommandType
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{ContentProviderIdentifier, User, Venue}
import java.time.Instant

case class CommandDb(
    id: Id[Command] = -1L,
    venueId: Id[Venue],
    userId: Option[Id[User]],
    queueCommandType: QueueCommandType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
