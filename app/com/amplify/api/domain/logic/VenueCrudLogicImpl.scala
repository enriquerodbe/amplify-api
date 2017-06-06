package com.amplify.api.domain.logic

import com.amplify.api.domain.models.EventSource.SetCurrentPlaylist
import com.amplify.api.domain.models.QueueEvent.{AddVenueTrack, RemoveVenueTracks}
import com.amplify.api.domain.models._
import com.amplify.api.services.{EventService, QueueService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueCrudLogicImpl @Inject()(
    venueService: VenueService,
    eventService: EventService,
    queueService: QueueService)(
    implicit ec: ExecutionContext) extends VenueCrudLogic {

  override def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[Playlist]] = {
    venueService.retrievePlaylists(venue)
  }

  override def setCurrentPlaylist(
      venueReq: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      playlist ← venueService.retrievePlaylistTracks(venueReq, playlistIdentifier)
      eventSource = SetCurrentPlaylist(venueReq.venue, playlistIdentifier)
      queueEvents = playlist.map(AddVenueTrack.apply) :+ RemoveVenueTracks
      _ ← eventService.create(eventSource, queueEvents: _*)
      _ ← queueService.update(venueReq.venue.unauthenticated, queueEvents: _*)
    }
    yield ()
  }

  override def retrieveQueue(venue: AuthenticatedVenue): Future[Queue] = {
    queueService.retrieve(venue)
  }

  override def retrieveAll(): Future[Seq[Venue]] = {
    venueService.retrieveAllVenues()
  }
}
