package com.amplify.api.aggregates.queue.daos

import com.amplify.api.aggregates.queue.CommandType.QueueCommandType
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

case class CommandDb(
    id: Id = -1L,
    venueId: Id,
    userId: Option[Id],
    queueCommandType: QueueCommandType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
