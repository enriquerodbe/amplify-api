package com.amplify.api.services

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {

  def requestRefreshAndAccessTokens(authorizationCode: AuthToken): Future[(Token, Token)]

  def fetchUser(implicit authToken: AuthToken): Future[UserData]
}
