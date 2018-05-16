package com.amplify.api.it

import com.amplify.api.it.fixtures.SpotifyContext
import com.amplify.api.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{PlaySpec, WsScalaTestClient}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.reflect.ClassTag

trait BaseHttpSpec
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
  override def beforeEach(): Unit = {
    Evolutions.applyEvolutions(database)
    mockSpotify()
  }
  override def afterEach(): Unit = {
    Evolutions.cleanupEvolutions(database)
    reset(spotifyContentProvider)
    reset(spotifyAuthProvider)
  }
  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().overrides(
      bind[SpotifyContentProvider].toInstance(spotifyContentProvider),
      bind[SpotifyAuthProvider].toInstance(spotifyAuthProvider))
      .build()
  }

  implicit val wsClient = app.injector.instanceOf[WSClient]
}
