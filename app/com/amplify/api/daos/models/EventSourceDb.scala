package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.EventSourceType.EventSourceType
import com.amplify.api.domain.models.{ContentProviderIdentifier, EventSource, User, Venue}
import java.time.Instant

case class EventSourceDb(
    id: Id[EventSource] = -1L,
    venueId: Id[Venue],
    userId: Option[Id[User]],
    eventType: EventSourceType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
