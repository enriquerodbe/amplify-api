package com.amplify.api.aggregates.queue.daos

import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

case class CommandDb(id: Id = -1L, venueId: Id, command: CommandDbData, createdAt: Instant)
