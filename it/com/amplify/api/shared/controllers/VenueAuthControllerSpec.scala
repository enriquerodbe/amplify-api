package com.amplify.api.shared.controllers

import com.amplify.api.domain.venue.auth.VenueAuthController
import com.amplify.api.shared.exceptions.{BadRequestException, UserAuthTokenNotFound}
import com.amplify.api.it.fixtures.DbVenueFixture
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import org.mockito.Mockito.verify
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class VenueAuthControllerSpec extends BaseIntegrationSpec with VenueRequests {

  val controller = instanceOf[VenueAuthController]

  class SignUpFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends DbVenueFixture

  "signIn" should {
    "respond OK" in {
      val response = controller.signIn()(venueRequest(bobCode))
      status(response) mustEqual OK
    }
    "respond with name" in {
      val response = contentAsJson(controller.signIn()(venueRequest(bobCode)))
      (response \ "name").as[String] mustEqual bobUserData.name.value
    }
    "respond with uid" in {
      val response = contentAsJson(controller.signIn()(venueRequest(bobCode)))
      (response \ "uid").as[String] must have size 8
    }
    "create venue" in new SignUpFixture {
      await(controller.signIn()(venueRequest(bobCode)))
      findVenues(bobUserData.name.value).headOption mustBe defined
    }
    "retrieve venue if it already exists" in new SignUpFixture {
      await(controller.signIn()(venueRequest(aliceCode).withAliceSession))
      findVenues(aliceDbVenue.name.value).headOption mustBe defined
    }

    "fail" when {
      "invalid authorization code provided" in {
        val response = controller.signIn()(venueRequest(invalidAuthCode))
        val exception = intercept[BadRequestException](status(response))
        exception mustEqual UserAuthTokenNotFound
      }
    }
  }

  class RetrieveCurrentFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "retrieveCurrent" should {
    "respond OK" in new RetrieveCurrentFixture {
      val response = controller.retrieveCurrent()(FakeRequest().withAliceSession)

      status(response) mustBe OK
    }
    "respond with venue" in new RetrieveCurrentFixture {
      val response = controller.retrieveCurrent()(FakeRequest().withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "name").as[String] mustEqual aliceDbVenue.name.toString
      (jsonResponse \ "uid").as[String] must have size 8
    }
    "refresh access token" in new RetrieveCurrentFixture {
      val response = controller.retrieveCurrent()(FakeRequest().withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "access_token").as[String] mustEqual aliceAccessToken.value

      verify(spotifyAuthProvider).refreshAccessToken(aliceRefreshToken)
    }
  }
}
