package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.{ContentProviderIdentifier, User}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def create(user: UserDb): DBIO[UserDb]

  def retrieve(
      identifier: ContentProviderIdentifier[User]): DBIO[Option[(UserDb, Option[VenueDb])]]
}
