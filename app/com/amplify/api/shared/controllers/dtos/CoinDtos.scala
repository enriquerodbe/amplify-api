package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.Coin
import play.api.libs.json.{JsValue, Json, Reads, Writes}

object CoinDtos extends DtosDefinition {

  case class CreateCoinsRequest(number: Int)
  implicit val createCoinsRequestReads: Reads[CreateCoinsRequest] = Json.reads[CreateCoinsRequest]

  case class CreateCoinResponse(code: String, remaining: Int) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def coinToCreateCoinResponse(coin: Coin): CreateCoinResponse = {
    CreateCoinResponse(coin.code.toString, coin.maxUsages)
  }
  implicit val createCoinResponseWrites: Writes[CreateCoinResponse] = {
    Json.writes[CreateCoinResponse]
  }

  case class CoinRemainingUsagesResponse(remaining: Int) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  implicit val coinRemainingUsagesResponseWrites: Writes[CoinRemainingUsagesResponse] = {
    Json.writes[CoinRemainingUsagesResponse]
  }
}
