package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{Playlist, User, Venue}
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
      name: Name[Venue],
      userData: UserData,
      authProviderType: ContentProviderType): Future[Unit] = {
    val userDb = userDataToUserDb(userData, authProviderType)

    val action =
      for {
        user ← userDao.create(userDb)
        _ ← venueDao.create(user, name)
      }
      yield ()

    db.runTransactionally(action)
  }

  override def retrievePlaylists(authToken: String)(implicit user: User): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }
}
