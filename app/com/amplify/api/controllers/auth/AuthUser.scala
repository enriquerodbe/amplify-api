package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.User

case class AuthUser(
    user: User,
    authToken: String,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  override def identifier: String = user.identifier.toString
}
