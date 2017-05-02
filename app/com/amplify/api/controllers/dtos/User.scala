package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.PrimitivesJsonConverters._
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import play.api.libs.json.{Json, Writes}

object User {

  case class UserResponse(name: String, email: String, identifier: ContentProviderIdentifier)
  def authenticatedUserToUserResponse(authUser: AuthenticatedUser): UserResponse = {
    UserResponse(
      name = authUser.name,
      email = authUser.email,
      identifier = authUser.identifier)
  }
  implicit val userResponseWrites: Writes[UserResponse] = Json.writes[UserResponse]
}
