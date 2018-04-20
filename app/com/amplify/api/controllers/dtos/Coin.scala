package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models
import play.api.libs.json.{Json, Reads, Writes}

object Coin {

  case class CreateCoinsRequest(number: Int)
  implicit val createCoinsRequestReads: Reads[CreateCoinsRequest] = Json.reads[CreateCoinsRequest]

  case class CoinResponse(token: String, remaining: Int)
  def coinToCoinResponse(coin: models.Coin): CoinResponse = {
    CoinResponse(coin.token.toString, coin.remainingUsages)
  }
  implicit val coinResponseWrites: Writes[CoinResponse] = Json.writes[CoinResponse]
}
