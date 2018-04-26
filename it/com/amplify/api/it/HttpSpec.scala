package com.amplify.api.it

import play.api.libs.json.Json
import play.api.test.Helpers._

class HttpSpec extends BaseHttpSpec {

  "The application" should {
    "respond ok" in {
      val response = await {
        wsUrl("/venues").post(Json.obj("code" → aliceCode))
      }
      response.status mustBe OK
      response.contentType mustBe JSON
    }
    "respond unauthorized" in {
      val response = await(wsUrl("/venues")
        .post(Json.obj("code" → invalidToken)))
      response.status mustBe UNAUTHORIZED
      response.contentType mustBe JSON
    }
    "respond bad request" in {
      val response = await(wsUrl("/venues")
        .withHttpHeaders(AUTHORIZATION → s"Bearer $aliceToken")
        .post(Json.obj("wrong" → "test")))
      response.status mustBe BAD_REQUEST
      response.contentType mustBe JSON
    }
    "respond forbidden" in {
      val response = await {
        wsUrl("/venues/me").withHttpHeaders(AUTHORIZATION → s"Bearer $aliceToken").get()
      }
      response.status mustBe FORBIDDEN
      response.contentType mustBe JSON
    }
    "respond not found" in {
      val response = await(wsUrl("/wrong/path").get)
      response.status mustBe NOT_FOUND
      response.contentType mustBe JSON
    }
    "set CORS headers" in {
      val response = await {
        wsUrl("/venues").withHttpHeaders(ORIGIN → "http://example.com").options()
      }
      response.status mustBe NOT_FOUND
      response.headers must contain (ACCESS_CONTROL_ALLOW_ORIGIN → Seq("http://example.com"))
    }
  }
}
