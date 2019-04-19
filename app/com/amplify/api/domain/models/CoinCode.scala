package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.shared.exceptions.InvalidCoinCode
import scala.util.{Failure, Random, Success, Try}

case class CoinCode private (venueUid: Uid, code: Code) {

  override def toString: String = s"$venueUid:$code"
}

object CoinCode {

  val DEFAULT_LENGTH = 16

  def generate(venueUid: Uid): CoinCode = {
    new CoinCode(venueUid, Code(Random.alphanumeric.take(DEFAULT_LENGTH).mkString))
  }

  private val regex = "(\\w+):(\\w+)".r

  def fromString(str: String): Try[CoinCode] = str match {
    case regex(venueUid, code) ⇒ Success(CoinCode(Uid(venueUid), Code(code)))
    case _ ⇒ Failure(InvalidCoinCode(str))
  }
}
