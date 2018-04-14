package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models.{Playlist, Queue}
import com.amplify.api.it.fixtures.{SpotifyContext, UserDbFixture, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, UserRequests}
import com.amplify.api.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.Helpers._

class VenuePlayerControllerSpec
  extends BaseIntegrationSpec with SpotifyContext with Inside with UserRequests {

  val controller = instanceOf[VenuePlayerController]
  val commandProcessor = findCommandProcessor(aliceVenueUid)

  class SkipFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends VenueDbFixture

  "skip" should {
    "respond No content" in new SkipFixture {
      val response = controller.skip()(fakeRequest().withAliceToken)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new SkipFixture {
      controller.skip()(fakeRequest().withAliceToken).await()

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
      val response = controller.finish()(fakeRequest().withAliceToken)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new FinishFixture {
      controller.finish()(fakeRequest().withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }

  class AddTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with UserDbFixture {
    val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq(toModelTrack(bedOfNailsTrack)))
    val newQueue = Queue.empty.copy(currentPlaylist = Some(playlist))
    initQueue(aliceVenueUid, newQueue)
  }

  "addTrack" should {
    "respond No content" in new AddTrackFixture {
      val request = addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withAliceToken
      val response = controller.addTrack(aliceVenueUid)(request)
      status(response) mustBe NO_CONTENT
    }
    "update queue next track" in new AddTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withAliceToken
      controller.addTrack(aliceVenueUid)(request).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      val nextItem = queue.futureItems.head
      nextItem.isUserTrack mustBe true
      nextItem.track.identifier mustEqual trackId
    }
  }
}
