package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def create(user: UserDb): DBIO[UserDb]

  def retrieve(
      identifier: ContentProviderIdentifier): DBIO[Option[(UserDb, Option[VenueDb])]]
}
