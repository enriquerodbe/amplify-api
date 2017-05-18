package com.amplify.api.services

import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier, UnauthenticatedVenue}
import com.amplify.api.services.external.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def get(
      identifier: ContentProviderIdentifier
  ): Future[(AuthenticatedUser, Option[UnauthenticatedVenue])]

  def getOrCreate(userData: UserData): Future[AuthenticatedUser]
}
