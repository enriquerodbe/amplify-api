package com.amplify.api.aggregates.queue.daos

import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

case class EventDb(id: Id = -1L, queueCommandId: Id, event: EventDbData, createdAt: Instant)
