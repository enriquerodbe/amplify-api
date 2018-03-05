package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao}
import com.amplify.api.domain.models.{AuthProviderIdentifier, User}
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToUser}
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
    db: DbioRunner,
    userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def retrieve(identifier: AuthProviderIdentifier): Future[Option[User]] = {
    val action = userDao.retrieve(identifier).map(_.map(userDbToUser))
    db.run(action)
  }

  override def retrieveOrCreate(userData: UserData): Future[User] = {
    val userDb = userDataToUserDb(userData)
    val createdUser = db.runTransactionally(userDao.retrieveOrCreate(userDb))
    createdUser.map(userDbToUser)
  }
}
