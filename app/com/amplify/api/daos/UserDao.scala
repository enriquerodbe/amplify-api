package com.amplify.api.daos

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.domain.models.primitives.Id
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def retrieve(id: Id): DBIO[Option[UserDb]]

  def retrieve(identifier: ContentProviderIdentifier): DBIO[Option[UserDb]]

  def retrieveOrCreate(userDb: UserDb): DBIO[UserDb]
}
