package com.amplify.api.domain.models

import com.amplify.api.domain.models.NotificationProviderType.NotificationProviderType
import com.amplify.api.domain.models.primitives.Identifier

case class NotificationProviderIdentifier(
    notificationProvider: NotificationProviderType,
    identifier: Identifier) {

  override def toString: String = {
    s"$notificationProvider${NotificationProviderIdentifier.SEPARATOR}$identifier"
  }
}

object NotificationProviderIdentifier {

  val SEPARATOR = ":"
}
