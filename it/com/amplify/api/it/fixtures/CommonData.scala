package com.amplify.api.it.fixtures

import com.amplify.api.domain.models.primitives.{Id, Identifier, Name, Uid}
import scala.language.implicitConversions

trait CommonData {

  val aliceDbUserId = 1L
  val aliceDbVenueId = 1L
  val aliceSpotifyId = "alice-spotify-id"
  val aliceVenueUid = "Fa84A3fl"

  val bobDbUserId = 2L
  val bobSpotifyId = "bob-spotify-id"

  implicit def string2Name(str: String): Name = Name(str)
  implicit def string2Identifier(str: String): Identifier = Identifier(str)
  implicit def string2Uid(str: String): Uid = Uid(str)
  implicit def long2Id(long: Long): Id = Id(long)
}
