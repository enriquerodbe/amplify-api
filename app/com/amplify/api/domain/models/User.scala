package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Id, Name}

sealed trait User {

  def name: Name
}

case class AuthenticatedUser(id: Id, name: Name, identifier: AuthProviderIdentifier) extends User

case class AuthenticatedUserReq(user: AuthenticatedUser, authToken: AuthToken) extends User {

  override def name: Name = user.name

  def identifier: AuthProviderIdentifier = user.identifier
}
