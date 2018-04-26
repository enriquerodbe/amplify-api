package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.Coin

case class CoinSubject(
    coin: Coin,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  override def identifier: String = coin.token.toString
}