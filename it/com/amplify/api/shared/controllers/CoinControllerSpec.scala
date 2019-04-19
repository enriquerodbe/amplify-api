package com.amplify.api.shared.controllers

import com.amplify.api.domain.coin.CoinController
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions.InvalidCreateCoinsRequestedNumber
import com.amplify.api.it.fixtures.{DbCoinFixture, DbVenueFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import org.scalatest.Inspectors.forAll
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
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
      val response =
        controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession)
      status(response) mustEqual OK
    }
    "respond with coins" in new CreateCoinsFixture {
      val response =
        controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).as[JsArray]
      jsonResponse.value.foreach { jsValue ⇒
        (jsValue \ "remaining").as[Int] mustBe 1
        val code = (jsValue \ "code").as[String]
        code must have size 25
        code must startWith (s"$aliceVenueUid:")
      }
    }
    "create coins" in new CreateCoinsFixture {
      await(controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession))

      val createdCoins = findCoins(aliceVenueUid)

      createdCoins must have size validRequestNumber + 1
      forAll(createdCoins) { coin ⇒
        coin.coinCode.toString must have size 25
        coin.coinCode.toString must startWith (s"$aliceVenueUid:")
        coin.maxUsages mustBe envConfig.coinsDefaultMaxUsages
      }
    }
    "fail" when {
      "zero requested" in new CreateCoinsFixture {
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(0).withAliceSession))
        }
      }
      "more than the maximum requested" in new CreateCoinsFixture {
        val requestNumber = envConfig.coinsCreateMax + 1
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(requestNumber).withAliceSession))
        }
      }
    }
  }

  class CoinStatusFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  "coinStatus" should {
    "respond OK" in new CoinStatusFixture {
      val response = controller.coinStatus()(FakeRequest().withBody(()).withValidCoin)
      status(response) mustEqual OK
    }
    "respond with status" in new CoinStatusFixture {
      val response = controller.coinStatus()(FakeRequest().withBody(()).withValidCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "venue_name").as[String] mustEqual aliceDbVenue.name.value
      (jsonResponse \ "remaining_usages").as[Int] mustEqual 1
    }
  }
}
