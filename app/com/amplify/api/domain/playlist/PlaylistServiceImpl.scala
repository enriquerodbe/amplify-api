package com.amplify.api.domain.playlist

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.venue.VenueService
import com.amplify.api.domain.venue.auth.VenueAuthService
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.FutureUtils._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PlaylistServiceImpl @Inject()(
    venueService: VenueService,
    venueAuthService: VenueAuthService,
    contentService: PlaylistExternalContentService)(
    implicit ec: ExecutionContext) extends PlaylistService {

  override def retrievePlaylists(venueUid: Uid): Future[Seq[PlaylistInfo]] = {
    val eventualVenue = venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
    eventualVenue.flatMap { venue ⇒
      val contentProviders = venue.contentProviders
      venueAuthService.withRefreshToken(venue)(contentService.fetchPlaylists(contentProviders, _))
    }
  }

  override def retrievePlaylist(venueUid: Uid, identifier: PlaylistIdentifier): Future[Playlist] = {
    for {
      venue ← venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
      playlistInfo ← retrievePlaylistInfo(venue, identifier)
      playlistTracks ← retrievePlaylistTracks(venue, identifier)
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  private def retrievePlaylistInfo(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[PlaylistInfo] = {
    venueAuthService.withRefreshToken(venue)(contentService.fetchPlaylist(playlistIdentifier, _))
  }

  private def retrievePlaylistTracks(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[Seq[Track]] = {
    venueAuthService.withRefreshToken(venue) {
      contentService.fetchPlaylistTracks(playlistIdentifier, _)
    }
  }

  override def retrieveTrack(venueUid: Uid, identifier: TrackIdentifier): Future[Track] = {
    val eventualVenue = venueService.retrieve(venueUid) ?! VenueNotFoundByUid(venueUid)
    eventualVenue.flatMap { venue =>
      venueAuthService.withRefreshToken(venue)(contentService.fetchTrack(identifier, _))
    }
  }
}
