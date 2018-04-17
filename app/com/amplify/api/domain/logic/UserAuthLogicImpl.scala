package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, User}
import com.amplify.api.services.{AuthenticationService, UserService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    userService: UserService)(
    implicit ec: ExecutionContext) extends UserAuthLogic {

  override def login(implicit authToken: AuthToken): Future[Option[User]] = {
    for {
      userData ← authService.fetchUser
      maybeUser ← userService.retrieve(userData.identifier)
    }
    yield maybeUser
  }
}
