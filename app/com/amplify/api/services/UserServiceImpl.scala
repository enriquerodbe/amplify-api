package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models.{AuthenticatedUser, AuthProviderIdentifier, UnauthenticatedVenue}
import com.amplify.api.exceptions.UserNotFoundByIdentifier
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.converters.VenueConverter.venueDbToVenue
import com.amplify.api.services.models.UserData
import com.amplify.api.utils.DbioUtils.DbioT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
    db: DbioRunner,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def retrieve(
      identifier: AuthProviderIdentifier
  ): Future[(AuthenticatedUser, Option[UnauthenticatedVenue])] = {
    val action =
      for {
        user ← userDao.retrieve(identifier) ?! UserNotFoundByIdentifier(identifier)
        venue ← venueDao.retrieve(user.id)
      }
      yield userDbToAuthenticatedUser(user) → venue.map(venueDbToVenue)

    db.run(action)
  }

  override def retrieveOrCreate(userData: UserData): Future[AuthenticatedUser] = {
    val userDb = userDataToUserDb(userData)
    val createdUser = db.runTransactionally(userDao.retrieveOrCreate(userDb))
    createdUser.map(userDbToAuthenticatedUser)
  }
}
