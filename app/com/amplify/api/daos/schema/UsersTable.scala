package com.amplify.api.daos.schema

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.{Id, Identifier, Name}

trait UsersTable extends BaseTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Users(tag: Tag) extends Table[UserDb](tag, "users") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Name]("name")
    def authProviderType = column[AuthProviderType]("auth_provider")
    def authIdentifier = column[Identifier]("auth_identifier")

    def contentProviderIdentifier =
      (authProviderType, authIdentifier) <>
        ((AuthProviderIdentifier.apply _).tupled, AuthProviderIdentifier.unapply)

    def * = (id, name, contentProviderIdentifier) <> (UserDb.tupled, UserDb.unapply)
  }

  lazy val usersTable = TableQuery[Users]
}
