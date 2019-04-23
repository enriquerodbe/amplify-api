package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}
import com.amplify.api.domain.models.Coin

private case class CoinSubject(
    coin: Coin,
    roles: List[Role] = Nil,
    permissions: List[Permission] = Nil) extends Subject {

  override def identifier: String = coin.code.toString
}
