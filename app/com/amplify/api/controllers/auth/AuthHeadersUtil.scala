package com.amplify.api.controllers.auth

import com.amplify.api.controllers.auth.AuthHeadersUtil._
import com.amplify.api.domain.models.CoinToken
import com.amplify.api.exceptions.MissingCoin
import javax.inject.Inject
import play.api.mvc.RequestHeader
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Try}

class AuthHeadersUtil @Inject()(implicit ec: ExecutionContext) {

  def getCoinFromHeaders(request: RequestHeader): Try[CoinToken] = {
    request.headers.get(COIN_PARAM) match {
      case Some(code) ⇒ CoinToken.fromString(code)
      case _ ⇒ Failure(MissingCoin)
    }
  }
}

object AuthHeadersUtil {

  val COIN_PARAM = "Coin"
  val VENUE_UID = "venue-uid"
}
