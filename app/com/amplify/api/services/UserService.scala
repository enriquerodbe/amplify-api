package com.amplify.api.services

import com.amplify.api.domain.models.{AuthenticatedUser, AuthProviderIdentifier, UnauthenticatedVenue}
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def retrieve(
      identifier: AuthProviderIdentifier): Future[(AuthenticatedUser, Option[UnauthenticatedVenue])]

  def retrieveOrCreate(userData: UserData): Future[AuthenticatedUser]
}
