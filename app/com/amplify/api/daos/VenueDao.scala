package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.{Identifier, Name}
import com.amplify.api.domain.models.{User, Venue}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def create(user: UserDb, name: Name[Venue]): DBIO[VenueDb]

  def retrieveAll: DBIO[Seq[VenueDb]]

  def retrieve(
      identifier: Identifier[User],
      authProviderType: AuthProviderType): DBIO[Option[VenueDb]]
}
