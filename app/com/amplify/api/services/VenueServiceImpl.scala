package com.amplify.api.services

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.{UserAlreadyHasVenue, VenueNotFoundByUid}
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylistInfo
import com.amplify.api.services.converters.TrackConverter.trackDataToTrack
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.{venueDbToAuthenticatedVenue, venueDbToVenue, venueReqToVenueDb}
import com.amplify.api.services.external.ContentProviderRegistry
import com.amplify.api.services.external.models.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO
import com.amplify.api.utils.DbioUtils.DbioT

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: String): Future[UnauthenticatedVenue] = {
    val maybeVenue = venueDao.retrieve(uid).map(_.map(venueDbToVenue))
    db.run(maybeVenue ?! VenueNotFoundByUid(uid))
  }

  override def retrieveOrCreate(
      userData: UserData,
      venueReq: VenueRequest): Future[AuthenticatedVenue] = {
    val action = retrieveOrCreateUserWithVenue(userData).flatMap {
      case (_, Some(venueDb)) ⇒ DBIO.failed(UserAlreadyHasVenue(venueDbToVenue(venueDb)))
      case (userDb, _) ⇒
        val venueDb = venueDao.create(venueReqToVenueDb(venueReq, userDb.id))
        venueDb.map(venueDbToAuthenticatedVenue(_, userDbToAuthenticatedUser(userDb)))
    }

    db.runTransactionally(action)
  }

  private def retrieveOrCreateUserWithVenue(userData: UserData) = {
    for {
      user ← userDao.retrieveOrCreate(userDataToUserDb(userData))
      maybeVenue ← venueDao.retrieve(user.id)
    }
    yield (user, maybeVenue)
  }

  override def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]] = {
    val strategy = registry.getStrategy(venue.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(venue.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylistInfo))
  }

  override def retrievePlaylistInfo(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[PlaylistInfo] = {
    implicit val token = venue.authToken
    val contentProvider = registry.getStrategy(playlistIdentifier.contentProvider)
    val userIdentifier = venue.userIdentifier.identifier
    val result = contentProvider.fetchPlaylist(userIdentifier, playlistIdentifier.identifier)
    result.map(playlistDataToPlaylistInfo)
  }

  override def retrievePlaylistTracks(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Seq[Track]] = {
    implicit val token = venue.authToken
    val strategy = registry.getStrategy(playlistIdentifier.contentProvider)
    val userIdentifier = venue.userIdentifier.identifier
    val eventualPlaylist =
      strategy.fetchPlaylistTracks(userIdentifier, playlistIdentifier.identifier)
    eventualPlaylist.map(_.map(trackDataToTrack))
  }

  override def retrievePlaylist(
      venue: AuthenticatedVenueReq,
      identifier: ContentProviderIdentifier): Future[Playlist] = {
    val eventualPlaylistInfo = retrievePlaylistInfo(venue, identifier)
    val eventualPlaylistTracks = retrievePlaylistTracks(venue, identifier)
    for {
      playlistInfo ← eventualPlaylistInfo
      playlistTracks ← eventualPlaylistTracks
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  override def retrieveAll(): Future[Seq[Venue]] = {
    db.run(venueDao.retrieveAllVenues()).map(_.map(venueDbToVenue))
  }
}
