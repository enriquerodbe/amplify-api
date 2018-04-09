package com.amplify.api.domain.models

import scala.language.implicitConversions

object AuthProviderType extends Enumeration {

  type AuthProviderType = Value

  val Spotify = Value(1, "spotify")

  val default = Spotify

  def find(name: String): Option[AuthProviderType] = {
    AuthProviderType.values.find(_.toString.equalsIgnoreCase(name))
  }
}
