package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist, Venue}
import com.amplify.api.exceptions.UserAlreadyHasVenue
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.venueDbToVenue
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def getOrCreate(userData: UserData, name: Name): Future[Venue] = {
    val action = getUserWithVenue(userData).flatMap {
      case (userDb, Some(venueDb)) ⇒
        val venue = venueDbToVenue(venueDb, userDbToAuthenticatedUser(userDb))
        DBIO.failed(UserAlreadyHasVenue(venue))
      case (userDb, _) ⇒
        val venueDb = venueDao.create(userDb, name)
        venueDb.map(venueDbToVenue(_, userDbToAuthenticatedUser(userDb)))
    }

    db.runTransactionally(action)
  }

  private def getUserWithVenue(userData: UserData) = {
    for {
      user ← userDao.retrieveOrCreate(userDataToUserDb(userData))
      maybeVenue ← venueDao.retrieve(user)
    }
    yield (user, maybeVenue)
  }

  override def retrievePlaylists(userReq: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(userReq.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(userReq.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }
}
