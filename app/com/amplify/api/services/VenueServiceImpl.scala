package com.amplify.api.services

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.exceptions.UserAlreadyHasVenue
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.TrackConverter.trackDataToTrack
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.{venueDbToAuthenticatedVenue, venueDbToVenue, venueReqToVenueDb}
import com.amplify.api.services.external.ContentProviderRegistry
import com.amplify.api.services.external.models.UserData
import com.amplify.api.utils.FutureUtils._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[AuthenticatedVenue] = {
    val action =
      for {
        venueDb ← venueDao.retrieve(uid) ?! new Exception //
        userDb ← userDao.retrieve(venueDb.userId)
      }
      yield AuthenticatedVenue(userDbToAuthenticatedUser(userDb), venueDbToVenue(venueDb))

    db.run(action)
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

  override def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(venue.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(venue.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }

  override def retrievePlaylistTracks(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Seq[Track]] = {
    val strategy = registry.getStrategy(playlistIdentifier.contentProvider)
    val eventualPlaylist = strategy.fetchPlaylistTracks(
      venue.user.identifier.identifier,
      playlistIdentifier.identifier)(
      venue.authToken)
    eventualPlaylist.map(_.map(trackDataToTrack))
  }

  override def retrieveAll(): Future[Seq[Venue]] = {
    db.run(venueDao.retrieveAllVenues()).map(_.map(venueDbToVenue))
  }
}
