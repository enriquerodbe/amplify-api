package com.amplify.api.controllers

import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.EventSourceType.SetCurrentPlaylist
import com.amplify.api.domain.models.QueueEventType.{AddVenueTrack, SetCurrentPlaylist ⇒ QueueSetCurrentPlaylist}
import com.amplify.api.domain.models.{ContentProviderIdentifier, QueueEventType}
import com.amplify.api.exceptions.UnexpectedResponse
import com.amplify.api.it.fixtures.{EventSourceDbFixture, QueueEventDbFixture, SpotifyContext, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import org.mockito.Mockito.when
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class VenueCrudControllerSpec extends BaseIntegrationSpec with SpotifyContext with VenueRequests {

  val controller = instanceOf[VenueCrudController]

  class RetrievePlaylistsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture

  "retrievePlaylists" should {
    "respond OK" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(FakeRequest().withAliceToken)

      status(response) mustEqual OK
    }
    "respond with playlists" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(FakeRequest().withAliceToken)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).head
      (jsonResponse \ "name").as[String] mustEqual alicePlaylistData.name.toString
      (jsonResponse \ "identifier").as[String] mustEqual alicePlaylistData.identifier.toString
      val image = (jsonResponse \ "images").head
      (image \ "url").as[String] mustEqual alicePlaylistImages.head.url
      (image \ "height").as[Int] mustEqual alicePlaylistImages.head.height.get
      (image \ "width").as[Int] mustEqual alicePlaylistImages.head.width.get
    }

    "fail" when {
      "Spotify responds with unexpectedly" in new RetrievePlaylistsFixture {
        when(spotifyProvider.fetchPlaylists(aliceAuthToken))
          .thenThrow(classOf[UnexpectedResponse])

        intercept[UnexpectedResponse] {
          controller.retrievePlaylists()(FakeRequest().withAliceToken).await()
        }
      }
    }
  }

  class SetCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with EventSourceDbFixture with QueueEventDbFixture

  "setCurrentPlaylist" should {
    "create event source" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val eventSources = findEventSources(aliceVenueDbId)

      eventSources must have size 1
      eventSources.head must have(
        'eventType (SetCurrentPlaylist),
        'contentIdentifier (Some(ContentProviderIdentifier(Spotify, alicePlaylistIdentifier)))
      )
    }
    "create queue events" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val eventSources = findEventSources(aliceVenueDbId)
      val queueEvents = findQueueEvents(eventSources.head.id)

      queueEvents must have size 3
      queueEvents(0) must have ('eventType (QueueEventType.RemoveVenueTracks))
      queueEvents(1) must have(
        'eventType (AddVenueTrack),
        'contentIdentifier (Some(poisonTrackData.identifier))
      )
      queueEvents(2) must have(
        'eventType (QueueSetCurrentPlaylist),
        'contentIdentifier (Some(ContentProviderIdentifier(Spotify, alicePlaylistIdentifier)))
      )
    }
    "update queue" ignore "TODO: Test this!"
  }
}
