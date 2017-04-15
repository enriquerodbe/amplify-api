package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.converters.UserConverter.userDataToUserDb
import com.amplify.api.services.converters.VenueConverter.venueDbToVenue
import com.amplify.api.services.external.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def create(
      name: Name[Venue],
      userData: UserData,
      authProviderType: AuthProviderType): Future[Unit] = {
    val userDb = userDataToUserDb(userData, authProviderType)

    val action =
      for {
        user ← userDao.create(userDb)
        _ ← venueDao.create(user, name)
      }
      yield ()

    db.runTransactionally(action)
  }

  override def listAll: Future[Seq[Venue]] = db.run(venueDao.retrieveAll.map(_.map(venueDbToVenue)))
}
