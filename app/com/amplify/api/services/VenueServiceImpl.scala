package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist}
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.UserConverter.userDataToUserDb
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def create(
      userData: UserData,
      name: Name): Future[Unit] = {
    val userDb = userDataToUserDb(userData)

    val action =
      for {
        user ← userDao.create(userDb)
        _ ← venueDao.create(user, name)
      }
      yield ()

    db.runTransactionally(action)
  }

  override def retrievePlaylists(userReq: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(userReq.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(userReq.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }
}
