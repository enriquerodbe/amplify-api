package com.amplify.api.services.converters

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.services.external.UserData

object UserConverter {

  def userDataToUserDb(userData: UserData, authProviderType: AuthProviderType): UserDb = {
    UserDb(
      name = userData.name,
      email = userData.email,
      authIdentifier = userData.identifier,
      authProviderType = authProviderType)
  }

  def userDbToUser(userDb: UserDb, venueDb: Option[VenueDb]): User = {
    User(userDb.name, userDb.email, venueDb.map(VenueConverter.venueDbToVenue))
  }

  def userDbToUser(t: (UserDb, Option[VenueDb])): User = userDbToUser(t._1, t._2)
}
