package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.AuthenticatedUser
import play.api.libs.json.{Json, Writes}

object User {

  case class UserResponse(name: String, email: String, identifier: String)
  def authenticatedUserToUserResponse(authUser: AuthenticatedUser): UserResponse = {
    UserResponse(authUser.name, authUser.email, authUser.identifier)
  }
  implicit val userResponseWrites: Writes[UserResponse] = Json.writes[UserResponse]
}
