package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}

case class AuthUser(identifier: String, roles: List[Role], permissions: List[Permission])
  extends Subject
