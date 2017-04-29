package com.amplify.api.services.converters

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.AuthenticatedUser
import com.amplify.api.services.external.UserData

object UserConverter {

  def userDataToUserDb(userData: UserData): UserDb = {
    UserDb(
      name = userData.name,
      email = userData.email,
      authIdentifier = userData.identifier)
  }

  def userDbToAuthenticatedUser(userDb: UserDb, venueDb: Option[VenueDb]): AuthenticatedUser = {
    AuthenticatedUser(
      userDb.name,
      userDb.email,
      userDb.authIdentifier)
  }

  def userDbToAuthenticatedUser(t: (UserDb, Option[VenueDb])): AuthenticatedUser = {
    userDbToAuthenticatedUser(t._1, t._2)
  }
}
