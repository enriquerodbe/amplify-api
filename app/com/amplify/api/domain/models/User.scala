package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class User(name: Name, identifier: AuthProviderIdentifier)

case class UserReq(user: User, authToken: AuthToken) {

  def name: Name = user.name

  def identifier: AuthProviderIdentifier = user.identifier
}
