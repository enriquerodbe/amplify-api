package com.amplify.api.controllers

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.exceptions.BadRequestException
import com.amplify.api.it.BaseIntegrationSpec
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class VenueAuthControllerSpec extends BaseIntegrationSpec with Results {

  val controller = app.injector.instanceOf[VenueAuthController]

  "VenueAuth signUp" should {
    "fail when no token provided" in {
      val request = VenueRequest("Super cool venue")
      val response = controller.signUp()(FakeRequest().withBody(request))
      intercept[BadRequestException] {
        status(response) mustBe BAD_REQUEST
      }

    }
  }
}
