package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.{User, UserReq}

case class AmplifyApiUser(
    userReq: UserReq,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  def user: User = userReq.user

  override def identifier: String = user.identifier.toString
}
