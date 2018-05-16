package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Access, Refresh, Token, Uid}
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.converters.VenueConverter.{dbVenueToVenue, userDataToDbVenue}
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
    db.run(venueDao.retrieveOrCreate(userDataToDbVenue(venueData))).map(dbVenueToVenue)
  }

  override def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]] = {
    withRefreshToken(venue)(contentService.fetchPlaylists(venue.contentProviders, _))
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
    withRefreshToken(venue)(contentService.fetchPlaylist(playlistIdentifier, _))
  }

  private def retrievePlaylistTracks(
      venue: Venue,
      playlistIdentifier: PlaylistIdentifier): Future[Seq[Track]] = {
    withRefreshToken(venue)(contentService.fetchPlaylistTracks(playlistIdentifier, _))
  }

  override def startPlayback(venue: Venue, tracks: Seq[TrackIdentifier]): Future[Unit] = {
    withRefreshToken(venue)(contentService.startPlayback(tracks, _))
  }

  private def withRefreshToken[T](venue: Venue)(f: Token[Access] ⇒ Future[T]): Future[T] = {
    f(venue.accessToken).recoverWith {
      case UserAuthTokenNotFound ⇒
        val refreshToken = AuthToken[Refresh](venue.identifier.authProvider, venue.refreshToken)
        for {
          accessToken ← authService.refreshAccessToken(refreshToken)
          _ ← db.run(venueDao.updateAccessToken(venue, accessToken))
          result ← f(accessToken)
        }
        yield result
    }
  }
}
