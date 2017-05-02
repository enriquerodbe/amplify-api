package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, AuthenticatedUser}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserAuthLogicImpl])
trait UserAuthLogic {

  def signUp(authToken: AuthToken): Future[AuthenticatedUser]

  def login(authToken: AuthToken): Future[AuthenticatedUser]
}
