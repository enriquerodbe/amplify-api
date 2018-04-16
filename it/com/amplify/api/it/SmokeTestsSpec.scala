package com.amplify.api.it

import com.amplify.api.it.fixtures.SpotifyContext
import com.amplify.api.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{PlaySpec, WsScalaTestClient}
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.reflect.ClassTag

class SmokeTestsSpec
  extends PlaySpec
    with SpotifyContext
    with GuiceOneServerPerSuite
    with MockitoSugar
    with Results
    with WsScalaTestClient
    with DefaultAwaitTimeout
    with FutureAwaits
    with BeforeAndAfterEach {

  protected def instanceOf[T: ClassTag]: T = app.injector.instanceOf[T]
  implicit val dbConfig = instanceOf[DatabaseConfigProvider]
  val database = instanceOf[DBApi].database("default")
  override def beforeEach(): Unit = Evolutions.applyEvolutions(database)
  override def afterEach(): Unit = Evolutions.cleanupEvolutions(database)
  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().overrides(
      bind[SpotifyContentProvider].toInstance(spotifyContentProvider),
      bind[SpotifyAuthProvider].toInstance(spotifyAuthProvider))
      .build()
  }

  implicit val wsClient = app.injector.instanceOf[WSClient]

  "The application" should {
    "respond ok" in {
      val response = await {
        wsUrl("/venues")
          .withHttpHeaders(AUTHORIZATION → s"Bearer $aliceToken")
          .post(Json.obj("name" → "test"))
      }
      response.status mustBe OK
      response.contentType mustBe JSON
    }
    "respond unauthorized" in {
      val response = await(wsUrl("/venues")
        .withHttpHeaders(AUTHORIZATION → s"Bearer $invalidToken")
        .post(Json.obj("name" → "test")))
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
