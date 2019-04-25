package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.AuthenticatedRequest
import be.objectify.deadbolt.scala.models.Subject
import com.amplify.api.domain.coin.CoinDeadboltHandler.COIN_PARAM
import com.amplify.api.domain.models.primitives.Code
import com.amplify.api.shared.controllers.auth.AbstractDeadboltHandler
import com.amplify.api.shared.exceptions.MissingCoin
import play.api.mvc.RequestHeader
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class CoinDeadboltHandler(coinService: CoinService)(implicit ec: ExecutionContext)
  extends AbstractDeadboltHandler {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    getCoinFromHeaders(request) match {
      case Success(coin) ⇒ coinService.login(coin).map(_.map(CoinSubject(_)))
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }

  private def getCoinFromHeaders(request: RequestHeader): Try[Code] = {
    request.headers.get(COIN_PARAM).map(c ⇒ Success(Code(c))).getOrElse(Failure(MissingCoin))
  }
}

object CoinDeadboltHandler {

  val COIN_PARAM = "Coin"
}
