package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.QueueCommandType.QueueCommandType
import com.amplify.api.domain.models.{ContentProviderIdentifier, QueueCommand, User, Venue}
import java.time.Instant

case class QueueCommandDb(
    id: Id[QueueCommand] = -1L,
    venueId: Id[Venue],
    userId: Option[Id[User]],
    eventType: QueueCommandType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
