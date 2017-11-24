package com.amplify.api.aggregates.queue.services.external

import com.amplify.api.aggregates.queue.services.external.firebase.FirebaseNotificationProvider
import com.amplify.api.domain.models.NotificationProviderType.{Firebase, NotificationProviderType}
import com.amplify.api.domain.models.primitives.Identifier
import javax.inject.Inject
import scala.concurrent.Future

trait NotificationStrategy {

  def pushNotification(notification: String, destination: Identifier): Future[Unit]
}

class NotificationProviderRegistry @Inject()(
    firebaseNotificationProvider: FirebaseNotificationProvider) {

  def getStrategy(notificationProvider: NotificationProviderType): NotificationStrategy = {
    notificationProvider match {
      case Firebase ⇒ firebaseNotificationProvider
    }
  }
}
