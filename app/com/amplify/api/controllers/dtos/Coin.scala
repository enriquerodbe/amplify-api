package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

object Coin {

  val MIN_COINS = 1
  val MAX_COINS = 100

  case class CreateCoinsRequest(number: Int)
  implicit val createCoinsRequestReads: Reads[CreateCoinsRequest] = {
    (JsPath \ "number").read[Int](min(MIN_COINS) andKeep max(MAX_COINS)).map(CreateCoinsRequest)
  }

  case class CoinResponse(token: String, remaining: Int)
  def coinToCoinResponse(coin: models.Coin): CoinResponse = {
    CoinResponse(coin.token.toString, coin.remainingUsages)
  }
  implicit val coinResponseWrites: Writes[CoinResponse] = Json.writes[CoinResponse]
}
