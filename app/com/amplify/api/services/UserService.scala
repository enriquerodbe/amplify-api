package com.amplify.api.services

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def get(userData: UserData, authProviderType: ContentProviderType): Future[User]
}
