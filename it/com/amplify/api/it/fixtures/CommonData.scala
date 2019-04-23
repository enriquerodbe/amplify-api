package com.amplify.api.it.fixtures

import com.amplify.api.domain.models.primitives._
import scala.language.implicitConversions

trait CommonData {

  val aliceSpotifyId = "alice-spotify-id"
  val aliceVenueUid = "Fa84A3fl"

  val aliceAccessToken = Token[Access]("alice-access-token")
  val aliceRefreshToken = Token[Refresh]("alice-refresh-token")
  val aliceCode = Token[AuthorizationCode]("alice-auth-code")
  val bobRefreshToken = Token[Refresh]("bob-refresh-token")
  val bobAccessToken = Token[Access]("bob-access-token")
  val bobCode = Token[AuthorizationCode]("alice-auth-code")
  val invalidAccessToken = Token[Access]("invalid-access-token")
  val invalidAuthCode = Token[AuthorizationCode]("invalid-auth-code")

  val validCoinCodeStr = "valid_coin123456"

  val bobSpotifyId = "bob-spotify-id"

  implicit def string2Name(str: String): Name = Name(str)
  implicit def string2Identifier(str: String): Identifier = Identifier(str)
  implicit def string2Uid(str: String): Uid = Uid(str)
  implicit def long2Id(long: Long): Id = Id(long)
}
