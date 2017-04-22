package com.amplify.api.domain.logic

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.User
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserAuthLogicImpl])
trait UserAuthLogic {

  def login(authProviderType: ContentProviderType, authToken: String): Future[User]
}
