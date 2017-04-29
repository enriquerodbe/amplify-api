package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.{AuthenticatedUser, AuthenticatedUserReq}

case class AmplifyApiSubject(
    userReq: AuthenticatedUserReq,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  def user: AuthenticatedUser = userReq.user

  override def identifier: String = user.identifier.toString
}
