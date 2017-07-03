package com.amplify.api.controllers

import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.exceptions.{BadRequestException, MissingAuthTokenHeader, UserAlreadyHasVenue, UserAuthTokenNotFound}
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{SpotifyContext, VenueDbFixture}
import play.api.libs.json.{JsDefined, JsString}
import play.api.test.Helpers._

class VenueAuthControllerSpec extends BaseIntegrationSpec with SpotifyContext {

  val controller = instanceOf[VenueAuthController]

  "signUp" should {
    "respond OK" in {
      val response = controller.signUp()(venueRequest("Test venue").withBobToken)

      status(response) mustEqual OK
    }
    "respond with name" in {
      val response = contentAsJson(controller.signUp()(venueRequest("Test venue").withBobToken))

      response \ "name" must matchPattern {
        case JsDefined(JsString(name)) if name == "Test venue" ⇒
      }
    }
    "respond with uid" in {
      val response = contentAsJson(controller.signUp()(venueRequest("Test venue").withBobToken))

      response \ "uid" must matchPattern {
        case JsDefined(JsString(uid)) if uid.trim.length == 8 ⇒
      }
    }

    "create venue" in new VenueDbFixture { import profile.api._
      controller.signUp()(venueRequest("Test venue").withBobToken).await()
      venuesTable.filter(_.name === Name("Test venue")).length.await() mustEqual 1
    }

    "create user" in new VenueDbFixture { import profile.api._
      controller.signUp()(venueRequest("Test venue").withBobToken).await()
      usersTable.filter(_.name === aliceUserData.name).length.await() mustEqual 1
    }

    "retrieve user if it already exists" in new VenueDbFixture { import profile.api._
      val userId = insertUser(bobUserDb)

      controller.signUp()(venueRequest("Test bar").withBobToken).await()

      val venue = db.run(venuesTable.filter(_.name === Name("Test bar")).result.headOption).await()
      venue.map(_.userId) must be(Some(userId))
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
        val response = controller.signUp()(venueRequest(null).withBobToken)
        intercept[Exception](status(response))
      }

      "user already has a venue" in new VenueDbFixture {
        controller.signUp()(venueRequest("Test venue").withBobToken).await()
        intercept[UserAlreadyHasVenue] {
          controller.signUp()(venueRequest("Test venue 2").withBobToken).await()
        }
      }
    }
  }
}
