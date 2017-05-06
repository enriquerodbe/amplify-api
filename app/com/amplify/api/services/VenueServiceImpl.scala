package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.UserAlreadyHasVenue
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.{venueDbToVenue, venueReqToVenueDb}
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

  override def getOrCreate(userData: UserData, venueReq: VenueReq): Future[AuthenticatedVenue] = {
    val action = getUserWithVenue(userData).flatMap {
      case (userDb, Some(venueDb)) ⇒
        val venue = venueDbToVenue(venueDb, userDbToAuthenticatedUser(userDb))
        DBIO.failed(UserAlreadyHasVenue(venue))
      case (userDb, _) ⇒
        val venueDb = venueDao.create(venueReqToVenueDb(venueReq, userDb.id))
        venueDb.map(venueDbToVenue(_, userDbToAuthenticatedUser(userDb)))
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

  override def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(user.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(user.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }

  override def setCurrentPlaylist(
      user: AuthenticatedUserReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    val action =
      for {
        venue ← venueDao.retrieve(user.user.identifier)
        result ← venueDao.updateCurrentPlaylist(venue.id, playlistIdentifier)
      }
      yield result

    db.run(action)
  }
}
