package com.amplify.api.daos.schema

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{User, Venue}
import com.amplify.api.domain.models.primitives.Name

trait VenuesTable extends BaseTable with UsersTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Venues(tag: Tag) extends Table[VenueDb](tag, "venues") {
    def id = column[Id[Venue]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Name]("name")
    def userId = column[Id[User]]("user_id")

    def user = foreignKey("user_fk", userId, usersTable)(_.id)

    def * = (id, name, userId) <> (VenueDb.tupled, VenueDb.unapply)
  }

  lazy val venuesTable = TableQuery[Venues]
}
