package com.amplify.api.services.converters

import com.amplify.api.daos.models.UserDb
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.services.external.UserData

object UserConverter {

  def userDataToUserDb(userData: UserData, authProviderType: AuthProviderType): UserDb = {
    UserDb(
      name = userData.name,
      email = userData.email,
      authIdentifier = userData.identifier,
      authProviderType = authProviderType)
  }
}
