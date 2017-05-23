package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, AuthenticatedUser, UnauthenticatedVenue}
import com.amplify.api.services.{AuthenticationService, UserService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    userService: UserService)(
    implicit ec: ExecutionContext) extends UserAuthLogic {

  override def signUp(implicit authToken: AuthToken): Future[AuthenticatedUser] = {
    for {
      userData ← authService.fetchUser
      user ← userService.getOrCreate(userData)
    }
    yield user
  }

  override def login(
      implicit authToken: AuthToken): Future[(AuthenticatedUser, Option[UnauthenticatedVenue])] = {
    for {
      userData ← authService.fetchUser
      userAndVenue ← userService.get(userData.identifier)
    }
    yield userAndVenue
  }
}
