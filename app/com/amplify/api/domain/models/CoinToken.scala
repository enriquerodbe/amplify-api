package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.exceptions.InvalidCoinToken
import scala.util.{Failure, Random, Success, Try}

case class CoinToken private (venueUid: Uid, token: String) {

  override def toString: String = s"$venueUid:$token"
}

object CoinToken {

  val DEFAULT_LENGTH = 16

  def generate(venueUid: Uid): CoinToken = {
    new CoinToken(venueUid, Random.alphanumeric.take(DEFAULT_LENGTH).mkString)
  }

  private val regex = "(\\w+):(\\w+)".r

  def fromString(str: String): Try[CoinToken] = str match {
    case regex(venueUid, token) ⇒ Success(CoinToken(Uid(venueUid), token))
    case _ ⇒ Failure(InvalidCoinToken(str))
  }
}
