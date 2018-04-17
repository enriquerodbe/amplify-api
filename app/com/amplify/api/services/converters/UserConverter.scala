package com.amplify.api.services.converters

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.User

object UserConverter {

  def userDbToUser(userDb: UserDb): User = User(userDb.name, userDb.authIdentifier)
}
