package com.amplify.api.services.converters

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.AuthenticatedUser
import com.amplify.api.services.models.UserData

object UserConverter {

  def userDataToUserDb(userData: UserData): UserDb = {
    UserDb(
      name = userData.name,
      authIdentifier = userData.identifier)
  }

  def userDbToAuthenticatedUser(userDb: UserDb): AuthenticatedUser = {
    AuthenticatedUser(userDb.id, userDb.name, userDb.authIdentifier)
  }
}
