package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.models.Queue
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{SpotifyContext, VenueDbFixture}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenuePlayerControllerSpec extends BaseIntegrationSpec with SpotifyContext with Inside {

  val controller = instanceOf[VenuePlayerController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueUid"
  val commandProcessor = app.actorSystem.actorSelection(path)

  class SkipFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends VenueDbFixture

  "skip" should {
    "respond No content" in new SkipFixture {
      val response = controller.skip()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }

  class FinishFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends VenueDbFixture

  "finish" should {
    "respond No content" in new FinishFixture {
      val response = controller.finish()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new FinishFixture {
      controller.finish()(FakeRequest().withBody(()).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }
}
