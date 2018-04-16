package com.amplify.api.controllers

import com.amplify.api.exceptions.{BadRequestException, MissingAuthTokenHeader, UserAuthTokenNotFound}
import com.amplify.api.it.fixtures.UserDbFixture
import com.amplify.api.it.{BaseIntegrationSpec, UserRequests}
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.Helpers._

class UserAuthControllerSpec extends BaseIntegrationSpec with UserRequests {

  val controller = instanceOf[UserAuthController]

  class SignUpFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends UserDbFixture

  "signUp" should {
    "respond OK" in {
      val response = controller.signUp()(fakeRequest().withAliceToken)
      status(response) mustEqual OK
    }
    "respond with name" in {
      val response = contentAsJson(controller.signUp()(fakeRequest().withBobToken))
      (response \ "name").as[String] mustEqual bobUserData.name.value
    }
    "respond with identifier" in {
      val response = contentAsJson(controller.signUp()(fakeRequest().withBobToken))
      (response \ "identifier").as[String] mustBe s"spotify:$bobSpotifyId"
    }
    "create user" in new SignUpFixture {
      controller.signUp()(fakeRequest().withBobToken).await()
      findUsers(bobUserData.name).headOption mustBe defined
    }
    "retrieve user if it already exists" in new SignUpFixture {
      controller.signUp()(fakeRequest().withAliceToken).await()
      findUsers(aliceUserData.name).headOption mustBe defined
    }

    "fail" when {
      "no Auth-token provided" in {
        val response = controller.signUp()(fakeRequest())
        val exception = intercept[BadRequestException](status(response))
        exception mustEqual MissingAuthTokenHeader
      }

      "invalid Auth-token provided" in {
        val response = controller.signUp()(fakeRequest().withAuthToken(invalidToken))
        val exception = intercept[BadRequestException](status(response))
        exception mustEqual UserAuthTokenNotFound
      }
    }
  }
}
