package com.amplify.api.controllers

import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{SpotifyContext, VenueDbFixture}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenueCrudControllerSpec extends BaseIntegrationSpec with SpotifyContext {

  val controller = instanceOf[VenueCrudController]

  "retrievePlaylists" should {
    "respond OK" in new VenueDbFixture {
      val response = controller.retrievePlaylists(0, 1)(FakeRequest().withAliceToken)
      status(response) mustEqual OK
    }
  }
}
