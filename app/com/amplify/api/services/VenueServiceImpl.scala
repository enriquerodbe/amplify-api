package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist, Venue}
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylist
import com.amplify.api.services.converters.UserConverter.userDbToAuthenticatedUser
import com.amplify.api.services.converters.VenueConverter.venueDbToVenue
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userService: UserService,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def getOrCreate(userData: UserData, name: Name): Future[Venue] = {
    val action =
      for {
        user ← userService.getOrCreateAction(userData)
        venue ← venueDao.create(user, name)
      }
      yield venueDbToVenue(venue, userDbToAuthenticatedUser(user))

    db.runTransactionally(action)
  }

  override def retrievePlaylists(userReq: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    val strategy = registry.getStrategy(userReq.user.identifier.contentProvider)
    val eventualPlaylists = strategy.fetchPlaylists(userReq.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylist))
  }
}
