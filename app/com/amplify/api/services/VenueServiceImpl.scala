package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Token, Uid}
import com.amplify.api.services.converters.VenueConverter.{dbVenueToVenue, userDataToDbVenue}
import com.amplify.api.services.external.ContentService
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    contentService: ContentService,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[Option[Venue]] = {
    db.run(venueDao.retrieve(uid).map(_.map(dbVenueToVenue)))
  }

  override def retrieveOrCreate(
      userData: UserData,
      refreshToken: Token,
      accessToken: Token): Future[Venue] = {
    val dbVenue = userDataToDbVenue(userData, refreshToken, accessToken)
    val createdVenue = db.runTransactionally(venueDao.retrieveOrCreate(dbVenue))
    createdVenue.map(dbVenueToVenue)
  }

  override def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]] = {
    contentService.fetchPlaylists(venue.contentProviders, venue.accessToken)
  }

  override def retrievePlaylist(
      venue: Venue,
      identifier: PlaylistIdentifier): Future[Playlist] = {
    val eventualPlaylistInfo = retrievePlaylistInfo(venue, identifier)
    val eventualPlaylistTracks = retrievePlaylistTracks(venue, identifier)
    for {
      playlistInfo ← eventualPlaylistInfo
      playlistTracks ← eventualPlaylistTracks
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  private def retrievePlaylistInfo(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[PlaylistInfo] = {
    contentService.fetchPlaylist(playlistIdentifier, venue.accessToken)
  }

  private def retrievePlaylistTracks(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[Seq[Track]] = {
    contentService.fetchPlaylistTracks(playlistIdentifier, venue.accessToken)
  }

  override def startPlayback(venue: Venue, tracks: Seq[TrackIdentifier]): Future[Unit] = {
    contentService.startPlayback(tracks, venue.accessToken)
  }
}
