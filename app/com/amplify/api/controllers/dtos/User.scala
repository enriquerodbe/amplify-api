package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.AuthenticatedUser
import play.api.libs.json.{Json, Writes}

object User extends DtosDefinition {

  case class UserResponse(name: String, identifier: String)
  def authenticatedUserToUserResponse(authUser: AuthenticatedUser): UserResponse = {
    UserResponse(authUser.name, authUser.identifier.toString)
  }
  implicit val userResponseWrites: Writes[UserResponse] = Json.writes[UserResponse]
}
