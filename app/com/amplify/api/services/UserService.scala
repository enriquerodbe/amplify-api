package com.amplify.api.services

import com.amplify.api.domain.models.{AuthProviderIdentifier, User}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def retrieve(identifier: AuthProviderIdentifier): Future[Option[User]]
}
