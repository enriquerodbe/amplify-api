package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.auth.AuthenticatedRequests
import com.amplify.api.controllers.dtos.Coin.{CreateCoinsRequest, coinStatusToCoinStatusResponse, coinToCoinResponse}
import com.amplify.api.domain.logic.CoinLogic
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class CoinController @Inject()(
    cc: ControllerComponents,
    coinLogic: CoinLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends AbstractController(cc) with AuthenticatedRequests {

  def createCoins() = authenticatedVenue(parse.json[CreateCoinsRequest]) { request ⇒
    coinLogic
      .createCoins(request.subject.venueReq.venue, request.body.number)
      .map(_.map(coinToCoinResponse))
  }

  def coinStatus() = authenticatedCoin(parse.empty) { request ⇒
    coinLogic
      .retrieveStatus(request.subject.coin)
      .map(coinStatusToCoinStatusResponse)
  }
}
