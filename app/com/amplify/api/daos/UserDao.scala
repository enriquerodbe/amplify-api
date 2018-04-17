package com.amplify.api.daos

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[UserDb]]
}
