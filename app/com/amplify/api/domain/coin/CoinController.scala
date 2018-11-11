package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.CoinDtos.{CreateCoinsRequest, coinStatusToCoinStatusResponse, coinToCoinResponse}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class CoinController @Inject()(
    cc: ControllerComponents,
    coinLogic: CoinLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with CoinAuthRequests with VenueAuthRequests {

  def createCoins() = authenticatedVenue(parse.json[CreateCoinsRequest]) { request ⇒
    coinLogic
      .createCoins(request.subject.venue, request.body.number)
      .map(_.map(coinToCoinResponse))
  }

  def coinStatus() = authenticatedCoin(parse.empty) { request ⇒
    coinLogic
      .retrieveStatus(request.subject.coin)
      .map(coinStatusToCoinStatusResponse)
  }
}
