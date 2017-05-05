package com.amplify.api.services.converters

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.AuthenticatedUser
import com.amplify.api.services.external.models.UserData

object UserConverter {

  def userDataToUserDb(userData: UserData): UserDb = {
    UserDb(
      name = userData.name,
      email = userData.email,
      authIdentifier = userData.identifier)
  }

  def userDbToAuthenticatedUser(userDb: UserDb): AuthenticatedUser = {
    AuthenticatedUser(userDb.name, userDb.email, userDb.authIdentifier)
  }
}
