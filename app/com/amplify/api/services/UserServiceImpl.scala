package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao}
import com.amplify.api.domain.models.{AuthProviderIdentifier, User}
import com.amplify.api.services.converters.UserConverter.dbUserToUser
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
    db: DbioRunner,
    userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def retrieve(identifier: AuthProviderIdentifier): Future[Option[User]] = {
    val action = userDao.retrieve(identifier).map(_.map(dbUserToUser))
    db.run(action)
  }
}
