package com.amplify.api.domain.models

import scala.language.implicitConversions

object ContentProviderType extends Enumeration {

  type ContentProviderType = Value

  val Spotify = Value(1, "spotify")

  val default = Spotify

  def find(name: String): Option[ContentProviderType] = {
    ContentProviderType.values.find(_.toString.equalsIgnoreCase(name))
  }
}
