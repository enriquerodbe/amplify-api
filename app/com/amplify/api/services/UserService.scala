package com.amplify.api.services

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future
import slick.dbio.DBIO

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def get(identifier: ContentProviderIdentifier): Future[AuthenticatedUser]

  def getOrCreate(userData: UserData): Future[AuthenticatedUser]

  private[services] def getOrCreateAction(userData: UserData): DBIO[UserDb]
}
