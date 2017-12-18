package com.amplify.api.daos.schema

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.primitives.{Id, Name, Token, Uid}

trait VenuesTable extends BaseTable with UsersTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Venues(tag: Tag) extends Table[VenueDb](tag, "venues") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Name]("name")
    def userId = column[Id]("user_id")
    def uid = column[Uid]("uid")
    def fcmToken = column[Option[Token]]("fcm_token")

    def user = foreignKey("user_fk", userId, usersTable)(_.id)

    def * = (id, name, userId, uid, fcmToken) <> (VenueDb.tupled, VenueDb.unapply)
  }

  lazy val venuesTable = TableQuery[Venues]
}
