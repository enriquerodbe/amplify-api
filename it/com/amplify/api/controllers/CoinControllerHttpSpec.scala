package com.amplify.api.controllers

import com.amplify.api.exceptions.AppExceptionCode
import com.amplify.api.it.BaseHttpSpec
import com.amplify.api.it.fixtures.DbVenueFixture
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.test.Helpers._

class CoinControllerHttpSpec extends BaseHttpSpec {

  val controller = instanceOf[CoinController]

  class CreateCoinsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "createCoins" should {
    "return bad request" in new CreateCoinsFixture {
      val response = await {
        wsUrl("/venues/me/coins")
          .withHttpHeaders(AUTHORIZATION → s"Bearer $aliceToken")
          .post(Json.obj("number" → -1))
      }

      response.status mustBe BAD_REQUEST
      val exceptionCode = (Json.parse(response.body) \ "code").as[Int]
      exceptionCode mustBe AppExceptionCode.InvalidCreateCoinsRequestedNumber.id
    }
  }
}
