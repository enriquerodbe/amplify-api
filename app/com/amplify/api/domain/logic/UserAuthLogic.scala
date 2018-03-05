package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, User}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserAuthLogicImpl])
trait UserAuthLogic {

  def signUp(implicit authToken: AuthToken): Future[User]

  def login(implicit authToken: AuthToken): Future[Option[User]]
}
