package com.amplify.api.controllers

import com.amplify.api.exceptions.{BadRequestException, MissingAuthTokenHeader, UserAuthTokenNotFound}
import com.amplify.api.it.fixtures.{SpotifyContext, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.Helpers._

class VenueAuthControllerSpec extends BaseIntegrationSpec with SpotifyContext with VenueRequests {

  val controller = instanceOf[VenueAuthController]

  class SignUpFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends VenueDbFixture

  "signUp" should {
    "respond OK" in {
      val response = controller.signUp()(venueRequest("Test venue").withBobToken)
      status(response) mustEqual OK
    }
    "respond with name" in {
      val response = contentAsJson(controller.signUp()(venueRequest("Test venue").withBobToken))
      (response \ "name").as[String] mustEqual "Test venue"
    }
    "respond with uid" in {
      val response = contentAsJson(controller.signUp()(venueRequest("Test venue").withBobToken))
      (response \ "uid").as[String] must have size 8
    }
    "create venue" in new SignUpFixture {
      controller.signUp()(venueRequest("Test venue").withBobToken).await()
      findVenues("Test venue").headOption mustBe defined
    }
    "retrieve venue if it already exists" in new SignUpFixture {
      controller.signUp()(venueRequest("Test bar").withAliceToken).await()
      findVenues(aliceVenueDb.name.value).headOption mustBe defined
    }

    "fail" when {
      "no Auth-token provided" in {
        val response = controller.signUp()(venueRequest("Test venue"))
        val exception = intercept[BadRequestException](status(response))
        exception mustEqual MissingAuthTokenHeader
      }

      "invalid Auth-token provided" in {
        val response = controller.signUp()(venueRequest("Test venue").withAuthToken(invalidToken))
        val exception = intercept[BadRequestException](status(response))
        exception mustEqual UserAuthTokenNotFound
      }

      "venue without name provided" in {
        intercept[Exception] {
          val response = controller.signUp()(venueRequest(null).withBobToken)
          status(response)
        }
      }
    }
  }
}
