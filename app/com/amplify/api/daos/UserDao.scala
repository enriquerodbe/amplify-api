package com.amplify.api.daos

import com.amplify.api.daos.models.DbUser
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[DbUser]]
}
