package com.amplify.api.it

import akka.pattern.ask
import akka.util.Timeout
import com.amplify.api.aggregates.queue.CommandProcessor.SetState
import com.amplify.api.controllers.VenueCrudController
import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import org.mockito.Mockito.{RETURNS_SMART_NULLS, withSettings}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results
import play.api.test.FakeRequest
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.reflect.ClassTag

trait BaseIntegrationSpec
  extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with Results with BeforeAndAfterEach {

  protected def instanceOf[T: ClassTag]: T = app.injector.instanceOf[T]

  val spotifyContentProvider =
    mock[SpotifyContentProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  val spotifyAuthProvider =
    mock[SpotifyAuthProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  implicit val dbConfig = instanceOf[DatabaseConfigProvider]
  val database = instanceOf[DBApi].database("default")

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().overrides(
      bind[SpotifyContentProvider].toInstance(spotifyContentProvider),
      bind[SpotifyAuthProvider].toInstance(spotifyAuthProvider))
      .build()
  }

  override def beforeEach(): Unit = {
    app.actorSystem.actorSelection(s"/user/queue-command-router/*") ! SetState(Queue.empty)
    Evolutions.applyEvolutions(database)
  }

  override def afterEach(): Unit = Evolutions.cleanupEvolutions(database)

  protected def findCommandProcessor(venueUid: Uid) = {
    val path = s"/user/queue-command-router/queue-command-processor-$venueUid"
    app.actorSystem.actorSelection(path)
  }

  protected def initQueue(venueUid: Uid, queue: Queue) = {
    implicit val timeout = Timeout(2.seconds)
    Await.ready(instanceOf[VenueCrudController]
      .retrieveCurrentPlaylist(venueUid.value)(FakeRequest()), timeout.duration)
    val processor = findCommandProcessor(venueUid)
    Await.ready((processor ? SetState(queue)).mapTo[Unit], timeout.duration)
  }
}
