package com.amplify.api.controllers

import com.amplify.api.domain.models.QueueCommandType
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{QueueCommandDbFixture, SpotifyContext, VenueDbFixture}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenuePlayerControllerSpec extends BaseIntegrationSpec with SpotifyContext with Inside {

  val controller = instanceOf[VenuePlayerController]

  class PlayFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with QueueCommandDbFixture

  "play" should {
    "respond No content" in new PlayFixture {
      val response = controller.play()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create queue command" in new PlayFixture {
      controller.play()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDb.id)

      inside(queueCommands) { case Seq(queueCommand) ⇒
          queueCommand must have(
            'userId (None),
            'eventType (QueueCommandType.StartPlayback),
            'contentIdentifier (None)
          )
      }
    }
  }

  class PauseFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with QueueCommandDbFixture

  "pause" should {
    "respond No content" in new PauseFixture {
      val response = controller.pause()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create queue command" in new PauseFixture {
      controller.pause()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDb.id)

      inside(queueCommands) { case Seq(queueCommand) ⇒
          queueCommand must have(
            'userId (None),
            'eventType (QueueCommandType.PausePlayback),
            'contentIdentifier (None)
          )
      }
    }
  }
}
