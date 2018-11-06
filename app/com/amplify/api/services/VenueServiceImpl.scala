package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Access, Token, Uid}
import com.amplify.api.services.converters.VenueConverter.{dbVenueToVenue, venueDataToDbVenue}
import com.amplify.api.services.external.{ExternalAuthService, ExternalContentService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    authService: ExternalAuthService,
    contentService: ExternalContentService,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[Option[Venue]] = {
    db.run(venueDao.retrieve(uid).map(_.map(dbVenueToVenue)))
  }

  override def retrieveOrCreate(venueData: VenueData): Future[Venue] = {
    db.run(venueDao.retrieveOrCreate(venueDataToDbVenue(venueData))).map(dbVenueToVenue)
  }

  override def retrievePlaylists(
      venue: Venue)(
      accessToken: Token[Access]): Future[Seq[PlaylistInfo]] = {
    contentService.fetchPlaylists(venue.contentProviders, accessToken)
  }

  override def retrievePlaylist(
      identifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[Playlist] = {
    val eventualPlaylistInfo = retrievePlaylistInfo(identifier)(accessToken)
    val eventualPlaylistTracks = retrievePlaylistTracks(identifier)(accessToken)
    for {
      playlistInfo ← eventualPlaylistInfo
      playlistTracks ← eventualPlaylistTracks
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  private def retrievePlaylistInfo(
      playlistIdentifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[PlaylistInfo] = {
    contentService.fetchPlaylist(playlistIdentifier, accessToken)
  }

  private def retrievePlaylistTracks(
      playlistIdentifier: PlaylistIdentifier)(
      accessToken: Token[Access]): Future[Seq[Track]] = {
    contentService.fetchPlaylistTracks(playlistIdentifier, accessToken)
  }

  override def startPlayback(
      tracks: Seq[TrackIdentifier])(
      accessToken: Token[Access]): Future[Unit] = contentService.startPlayback(tracks, accessToken)
}
