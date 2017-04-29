package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Email, Name}

sealed trait User {

  def name: Name

  def email: Email
}

case class AuthenticatedUser(
    name: Name,
    email: Email,
    identifier: ContentProviderIdentifier) extends User

case class AuthenticatedUserReq(user: AuthenticatedUser, authToken: String) extends User {

  override def name: Name = user.name

  override def email: Email = user.email
}
