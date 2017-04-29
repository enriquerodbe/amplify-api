package com.amplify.api.services

import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def create(user: UserData): Future[Unit]

  def get(identifier: ContentProviderIdentifier): Future[AuthenticatedUser]
}
