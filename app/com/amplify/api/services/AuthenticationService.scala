package com.amplify.api.services

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {

  def fetchUser(authToken: AuthToken): Future[UserData]
}
