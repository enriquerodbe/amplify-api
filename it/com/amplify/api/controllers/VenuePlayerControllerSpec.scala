package com.amplify.api.controllers

import com.amplify.api.domain.models.EventSourceType
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{EventSourceDbFixture, SpotifyContext, VenueDbFixture}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenuePlayerControllerSpec extends BaseIntegrationSpec with SpotifyContext with Inside {

  val controller = instanceOf[VenuePlayerController]

  class PlayFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with EventSourceDbFixture

  "play" should {
    "respond No content" in new PlayFixture {
      val response = controller.play()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create event source" in new PlayFixture {
      controller.play()(FakeRequest().withBody(()).withAliceToken).await()

      val eventSources = findEventSources(aliceVenueDb.id)

      inside(eventSources) { case Seq(eventSource) ⇒
          eventSource must have(
            'userId (None),
            'eventType (EventSourceType.StartPlayback),
            'contentIdentifier (None)
          )
      }
    }
  }

  class PauseFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with EventSourceDbFixture

  "pause" should {
    "respond No content" in new PauseFixture {
      val response = controller.pause()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create event source" in new PauseFixture {
      controller.pause()(FakeRequest().withBody(()).withAliceToken).await()

      val eventSources = findEventSources(aliceVenueDb.id)

      inside(eventSources) { case Seq(eventSource) ⇒
          eventSource must have(
            'userId (None),
            'eventType (EventSourceType.PausePlayback),
            'contentIdentifier (None)
          )
      }
    }
  }
}
