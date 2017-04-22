package com.amplify.api.daos.schema

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.{ContentProviderIdentifier, User}
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name}

trait UsersTable extends BaseTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Users(tag: Tag) extends Table[UserDb](tag, "users") {
    def id = column[Id[User]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Name[User]]("name")
    def email = column[Email]("email")
    def authProviderType = column[ContentProviderType]("auth_provider")
    def authIdentifier = column[Identifier[User]]("auth_identifier")

    def contentProviderIdentifier =
      (authProviderType, authIdentifier) <>
        ((ContentProviderIdentifier.apply[User] _).tupled, ContentProviderIdentifier.unapply[User])

    def * = (id, name, email, contentProviderIdentifier) <> (UserDb.tupled, UserDb.unapply)
  }

  lazy val usersTable = TableQuery[Users]
}
