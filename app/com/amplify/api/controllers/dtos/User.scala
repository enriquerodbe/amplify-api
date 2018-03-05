package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{User ⇒ ModelUser}
import play.api.libs.json.{Json, Writes}

object User extends DtosDefinition {

  case class UserResponse(name: String, identifier: String)
  def authenticatedUserToUserResponse(authUser: ModelUser): UserResponse = {
    UserResponse(authUser.name, authUser.identifier.toString)
  }
  implicit val userResponseWrites: Writes[UserResponse] = Json.writes[UserResponse]
}
