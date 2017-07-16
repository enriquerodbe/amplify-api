package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.QueueEventType.QueueEventType
import com.amplify.api.domain.models._
import java.time.Instant

case class QueueEventDb(
    id: Id[QueueEvent] = -1L,
    queueCommandId: Id[QueueCommand],
    eventType: QueueEventType,
    contentIdentifier: Option[ContentProviderIdentifier],
    createdAt: Instant)
