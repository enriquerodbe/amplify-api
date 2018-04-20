package com.amplify.api.controllers

import com.amplify.api.configuration.EnvConfig
import com.amplify.api.exceptions.InvalidCreateCoinsRequestedNumber
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import com.amplify.api.it.fixtures.{DbCoinFixture, DbVenueFixture}
import org.scalatest.Inspectors.forAll
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.Helpers._
import play.mvc.Http

class CoinControllerSpec extends BaseIntegrationSpec with VenueRequests {

  val controller = instanceOf[CoinController]
  val envConfig = instanceOf[EnvConfig]
  val validRequestNumber = 2

  class CreateCoinsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  "createCoins" should {
    "respond OK" in new CreateCoinsFixture {
      val response = controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceToken)
      status(response) mustEqual OK
    }
    "respond with coins" in new CreateCoinsFixture {
      val response = controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceToken)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).as[JsArray]
      jsonResponse.value.foreach { jsValue ⇒
        (jsValue \ "remaining").as[Int] mustBe 1
        val token = (jsValue \ "token").as[String]
        token must have size 25
        token must startWith (s"$aliceVenueUid:")
      }
    }
    "create coins" in new CreateCoinsFixture {
      await(controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceToken))

      val createdCoins = findCoins(aliceDbVenueId)

      createdCoins must have size validRequestNumber
      forAll(createdCoins) { coin ⇒
        coin.token.toString must have size 25
        coin.token.toString must startWith (s"$aliceVenueUid:")
        coin.maxUsages mustBe envConfig.coinsDefaultMaxUsages
      }
    }
    "fail" when {
      "zero requested" in new CreateCoinsFixture {
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(0).withAliceToken))
        }
      }
      "more than the maximum requested" in new CreateCoinsFixture {
        val requestNumber = envConfig.coinsCreateMax + 1
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(requestNumber).withAliceToken))
        }
      }
    }
  }
}
