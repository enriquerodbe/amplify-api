package com.amplify.api.it

import com.amplify.api.aggregates.queue.MaterializedView.SetState
import com.amplify.api.domain.models.Queue
import com.amplify.api.aggregates.queue.services.external.firebase.FirebaseNotificationProvider
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
import scala.reflect.ClassTag

trait BaseIntegrationSpec
  extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with Results with BeforeAndAfterEach {

  protected def instanceOf[T: ClassTag]: T = app.injector.instanceOf[T]

  val spotifyContentProvider =
    mock[SpotifyContentProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  val spotifyAuthProvider =
    mock[SpotifyAuthProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  val firebaseNotificationProvider =
    mock[FirebaseNotificationProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  implicit val dbConfig = instanceOf[DatabaseConfigProvider]
  val database = instanceOf[DBApi].database("default")

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().overrides(
      bind[SpotifyContentProvider].toInstance(spotifyContentProvider),
      bind[SpotifyAuthProvider].toInstance(spotifyAuthProvider),
      bind[FirebaseNotificationProvider].toInstance(firebaseNotificationProvider))
      .build()
  }

  override def beforeEach(): Unit = {
    app.actorSystem.actorSelection(s"/user/queue-command-router/*") ! SetState(Queue.empty)
    Evolutions.applyEvolutions(database)
  }

  override def afterEach(): Unit = Evolutions.cleanupEvolutions(database)
}
