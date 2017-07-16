package com.amplify.api.domain.logic

import com.amplify.api.domain.models.QueueCommand.SetCurrentPlaylist
import com.amplify.api.domain.models.QueueEvent.{VenueTrackAdded, VenueTracksRemoved, CurrentPlaylistSet ⇒ QueueSetCurrentPlaylist}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.exceptions.CurrentPlaylistNotSet
import com.amplify.api.services.{QueueEventService, QueueService, VenueService}
import com.amplify.api.utils.FutureUtils._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueCrudLogicImpl @Inject()(
    venueService: VenueService,
    eventService: QueueEventService,
    queueService: QueueService)(
    implicit ec: ExecutionContext) extends VenueCrudLogic {

  override def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]] = {
    venueService.retrievePlaylists(venue)
  }

  override def setCurrentPlaylist(
      venueReq: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    for {
      playlist ← venueService.retrievePlaylist(venueReq, playlistIdentifier)
      tracksEvents = playlist.tracks.map(VenueTrackAdded.apply)
      queueEvents = VenueTracksRemoved +: tracksEvents :+ QueueSetCurrentPlaylist(playlist)
      queueCommand = SetCurrentPlaylist(venueReq.venue, playlistIdentifier)
      _ ← eventService.create(queueCommand, queueEvents: _*)
      _ ← queueService.update(venueReq.venue.unauthenticated, queueEvents: _*)
    }
    yield ()
  }

  override def retrieveCurrentPlaylistTracks(
      uid: Uid,
      user: AuthenticatedUserReq): Future[Seq[Track]] = {
    for {
      venue ← venueService.retrieve(uid)
      queue ← queueService.retrieve(venue.id)
      currentPlaylist ← queue.currentPlaylist ?! CurrentPlaylistNotSet(uid)
      venueReq = AuthenticatedVenueReq(venue, user.authToken)
      tracks ← venueService.retrievePlaylistTracks(venueReq, currentPlaylist.identifier.identifier)
    }
    yield tracks
  }

  override def retrieveQueue(venue: UnauthenticatedVenue): Future[Queue] = {
    queueService.retrieve(venue.id)
  }

  override def retrieveAll(): Future[Seq[Venue]] = venueService.retrieveAll()
}
