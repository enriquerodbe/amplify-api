package com.amplify.api.services.converters

import com.amplify.api.daos.models.DbUser
import com.amplify.api.domain.models.User

object UserConverter {

  def dbUserToUser(dbUser: DbUser): User = User(dbUser.name, dbUser.authIdentifier)
}
