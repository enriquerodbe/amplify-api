package com.amplify.api.it.fixtures

import com.amplify.api.domain.models.primitives._
import scala.language.implicitConversions

trait CommonData {

  val aliceDbUserId = 1L
  val aliceDbVenueId = 1L
  val aliceSpotifyId = "alice-spotify-id"
  val aliceVenueUid = "Fa84A3fl"

  val aliceToken = "alice-token"
  val aliceRefreshToken = "alice-refresh-token"
  val bobRefreshToken = "bob-refresh-token"
  val bobToken = "bob-token"
  val invalidToken = "invalid-token"

  val aliceCode = "alice-auth-code"
  val bobCode = "alice-auth-code"

  val validDbCoinId = 1L
  val validCoinTokenStr = "valid_coin123456"

  val bobDbUserId = 2L
  val bobSpotifyId = "bob-spotify-id"

  implicit def string2Name(str: String): Name = Name(str)
  implicit def string2Identifier(str: String): Identifier = Identifier(str)
  implicit def string2Uid(str: String): Uid = Uid(str)
  implicit def string2Token(str: String): Token = Token(str)
  implicit def long2Id(long: Long): Id = Id(long)
}
