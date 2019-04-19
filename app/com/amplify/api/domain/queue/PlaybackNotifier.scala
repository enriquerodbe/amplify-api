package com.amplify.api.domain.queue

import akka.actor.Actor
import com.amplify.api.domain.playlist.PlaylistExternalContentService
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.domain.venue.auth.VenueAuthService
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.FutureUtils._
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PlaybackNotifier @Inject()(
    contentService: PlaylistExternalContentService,
    venueService: VenueService,
    venueAuthService: VenueAuthService)(
    implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case QueueUpdated(PlaybackStarted(venueUid), queue) ⇒
      val eventualVenue = venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
      eventualVenue.flatMap { venue ⇒
        val trackIdentifiers = queue.futureItems.map(_.track.identifier)
        venueAuthService.withRefreshToken(venue)(contentService.startPlayback(trackIdentifiers, _))
      }
  }

  override def preStart(): Unit = context.system.eventStream.subscribe(self, classOf[QueueUpdated])
}
