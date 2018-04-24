package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.Queue.{QueueTrackResponse, itemToQueueTrackResponse}
import com.amplify.api.domain.models
import com.amplify.api.domain.models.CoinStatus
import play.api.libs.json.{JsValue, Json, Reads, Writes}

object Coin extends DtosDefinition {

  case class CreateCoinsRequest(number: Int)
  implicit val createCoinsRequestReads: Reads[CreateCoinsRequest] = Json.reads[CreateCoinsRequest]

  case class CoinResponse(token: String, remaining: Int) extends SuccessfulResponse {

    override def toJson: JsValue = Json.toJson(this)
  }
  def coinToCoinResponse(coin: models.Coin): CoinResponse = {
    CoinResponse(coin.token.toString, coin.remainingUsages)
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
