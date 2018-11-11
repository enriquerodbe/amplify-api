package com.amplify.api.domain.coin

import be.objectify.deadbolt.scala.AuthenticatedRequest
import be.objectify.deadbolt.scala.models.Subject
import com.amplify.api.domain.coin.CoinDeadboltHandler.COIN_PARAM
import com.amplify.api.domain.models.CoinToken
import com.amplify.api.shared.controllers.auth.AbstractDeadboltHandler
import com.amplify.api.shared.exceptions.MissingCoin
import play.api.mvc.RequestHeader
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class CoinDeadboltHandler(coinLogic: CoinLogic)(implicit ec: ExecutionContext)
  extends AbstractDeadboltHandler {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    getCoinFromHeaders(request) match {
      case Success(coin) ⇒ coinLogic.login(coin).map(_.map(CoinSubject(_)))
      case Failure(ex) ⇒ Future.failed(ex)
    }
  }

  private def getCoinFromHeaders(request: RequestHeader): Try[CoinToken] = {
    request.headers.get(COIN_PARAM) match {
      case Some(code) ⇒ CoinToken.fromString(code)
      case _ ⇒ Failure(MissingCoin)
    }
  }
}

object CoinDeadboltHandler {

  val COIN_PARAM = "Coin"
}
