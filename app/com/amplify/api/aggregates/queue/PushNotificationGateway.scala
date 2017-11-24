package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.CommandProcessor.AddTrack
import com.amplify.api.aggregates.queue.services.external.NotificationProviderRegistry
import com.amplify.api.domain.models.NotificationProviderType
import javax.inject.Inject

class PushNotificationGateway @Inject()(
    notificationProviderRegistry: NotificationProviderRegistry) extends Actor {

  override def receive: Receive = {
    case AddTrack(venue, _, _) ⇒
      venue.fcmToken.foreach { token ⇒
        val strategy = notificationProviderRegistry.getStrategy(NotificationProviderType.Firebase)
        strategy.pushNotification("TrackAdded", token.value)
      }

    case _ ⇒
  }
}
