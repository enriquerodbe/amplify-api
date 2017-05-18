package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.UserAlreadyHasVenue
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.TrackConverter.trackDataToTrack
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.{venueDbToAuthenticatedVenue, venueReqToVenueDb}
import com.amplify.api.services.external.ContentProviderRegistry
import com.amplify.api.services.external.models.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def getOrCreate(
      userData: UserData,
      venueReq: VenueReq): Future[AuthenticatedVenue] = {
    val action = getUserWithVenue(userData).flatMap {
      case (_, Some(venueDb)) ⇒ DBIO.failed(UserAlreadyHasVenue(VenueReq(venueDb.name)))
      case (userDb, _) ⇒
        val venueDb = venueDao.create(venueReqToVenueDb(venueReq, userDb.id))
        venueDb.map(venueDbToAuthenticatedVenue(_, userDbToAuthenticatedUser(userDb)))
    }

    db.runTransactionally(action)
  }

  private def getUserWithVenue(userData: UserData) = {
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
}
