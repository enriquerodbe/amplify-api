package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.domain.models.primitives.Identifier
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def create(user: UserDb): DBIO[UserDb]

  def retrieve(
      identifier: Identifier[User],
      authProviderType: AuthProviderType): DBIO[Option[(UserDb, Option[VenueDb])]]
}
