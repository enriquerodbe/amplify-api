package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.{Coin, CoinStatus}
import com.amplify.api.shared.controllers.dtos.QueueDtos.{QueueTrackResponse, itemToQueueTrackResponse}
import play.api.libs.json.{JsValue, Json, Reads, Writes}

object CoinDtos extends DtosDefinition {

  case class CreateCoinsRequest(number: Int)
  implicit val createCoinsRequestReads: Reads[CreateCoinsRequest] = Json.reads[CreateCoinsRequest]

  case class CoinResponse(code: String, remaining: Int) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def coinToCoinResponse(coin: Coin): CoinResponse = {
    CoinResponse(coin.code.toString, coin.remainingUsages)
  }
  implicit val coinResponseWrites: Writes[CoinResponse] = Json.writes[CoinResponse]

  case class CoinStatusResponse(
      venueName: String,
      currentTrack: Option[QueueTrackResponse],
      remainingUsages: Int) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def coinStatusToCoinStatusResponse(coinStatus: CoinStatus): CoinStatusResponse = {
    CoinStatusResponse(
      coinStatus.venue.name.value,
      coinStatus.currentTrack.map(itemToQueueTrackResponse),
      coinStatus.coin.remainingUsages)
  }
  implicit val coinStatusResponseWrites: Writes[CoinStatusResponse] = {
    Json.writes[CoinStatusResponse]
  }
}
