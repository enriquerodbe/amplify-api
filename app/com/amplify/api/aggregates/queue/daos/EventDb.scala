package com.amplify.api.aggregates.queue.daos

import com.amplify.api.aggregates.queue.EventType.QueueEventType
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

case class EventDb(
    id: Id = -1L,
    queueCommandId: Id,
    queueEventType: QueueEventType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
