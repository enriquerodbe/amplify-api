package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.Event.UserTrackAdded
import com.amplify.api.aggregates.queue.PushNotificationGateway.NotifyEvents
import com.amplify.api.aggregates.queue.services.external.NotificationProviderRegistry
import com.amplify.api.domain.models.{NotificationProviderType, Venue}
import javax.inject.Inject

class PushNotificationGateway @Inject()(
    notificationProviderRegistry: NotificationProviderRegistry) extends Actor {

  override def receive: Receive = {
    case NotifyEvents(venue, events) if events.exists(_.isInstanceOf[UserTrackAdded]) ⇒
      venue.fcmToken.foreach { token ⇒
        val strategy = notificationProviderRegistry.getStrategy(NotificationProviderType.Firebase)
        strategy.pushNotification("TrackAdded", token.value)
      }

    case _ ⇒
  }
}

object PushNotificationGateway {

  sealed trait PushNotificationGatewayProtocol

  case class NotifyEvents(venue: Venue, events: Seq[Event]) extends PushNotificationGatewayProtocol
}
