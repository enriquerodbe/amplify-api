package com.amplify.api.daos.schema

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives._

trait VenuesTable extends BaseTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Venues(tag: Tag) extends Table[VenueDb](tag, "venues") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Name]("name")
    def uid = column[Uid]("uid")
    def authProviderType = column[AuthProviderType]("auth_provider")
    def authIdentifier = column[Identifier]("auth_identifier")

    def authProviderIdentifier =
      (authProviderType, authIdentifier) <>
        ((AuthProviderIdentifier.apply _).tupled, AuthProviderIdentifier.unapply)

    def * = (id, name, uid, authProviderIdentifier) <> (VenueDb.tupled, VenueDb.unapply)
  }

  lazy val venuesTable = TableQuery[Venues]
  lazy val insertVenuesQuery =
    venuesTable.returning(venuesTable.map(_.id)).into((obj, id) ⇒ obj.copy(id = id))
}
