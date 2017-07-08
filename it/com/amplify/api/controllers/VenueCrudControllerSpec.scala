package com.amplify.api.controllers

import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.EventSourceType.SetCurrentPlaylist
import com.amplify.api.domain.models.QueueEventType.{AddVenueTrack, SetCurrentPlaylist ⇒ QueueSetCurrentPlaylist}
import com.amplify.api.domain.models.{ContentProviderIdentifier, Playlist, QueueEventType, QueueItemType}
import com.amplify.api.exceptions.{InvalidProviderIdentifier, UnexpectedResponse}
import com.amplify.api.it.fixtures.{EventSourceDbFixture, QueueEventDbFixture, SpotifyContext, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import com.amplify.api.services.QueueService
import org.mockito.Mockito.when
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class VenueCrudControllerSpec
  extends BaseIntegrationSpec with SpotifyContext with VenueRequests with Inside {

  val controller = instanceOf[VenueCrudController]
  val queueService = instanceOf[QueueService]

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
    "respond No content" in new SetCurrentPlaylistFixture {
      val response = controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken)

      status(response) mustEqual NO_CONTENT
    }
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

      queueEvents must have size 4
      queueEvents(0) must have ('eventType (QueueEventType.RemoveVenueTracks))
      queueEvents(1) must have(
        'eventType (AddVenueTrack),
        'contentIdentifier (Some(poisonTrackData.identifier))
      )
      queueEvents(2) must have(
        'eventType (AddVenueTrack),
        'contentIdentifier (Some(bedOfNailsTrackData.identifier))
      )
      queueEvents(3) must have(
        'eventType (QueueSetCurrentPlaylist),
        'contentIdentifier (Some(ContentProviderIdentifier(Spotify, alicePlaylistIdentifier)))
      )
    }
    "update queue current playlist" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = queueService.retrieve(aliceVenueDb.id).await()

      inside(queue.currentPlaylist) { case Some(Playlist(playlistInfo, tracks)) ⇒
        playlistInfo must have(
          'name (alicePlaylistData.name.toString),
          'identifier (alicePlaylistData.identifier)
        )
        playlistInfo.images must have size alicePlaylistData.images.size
        for (i ← playlistInfo.images.indices) {
          val playlistImage = alicePlaylistData.images(i)
          playlistInfo.images(i) must have(
            'url (playlistImage.url),
            'width (playlistImage.width),
            'height (playlistImage.height)
          )
        }

        tracks must have size alicePlaylistTracks.size
        for (trackIndex ← tracks.indices) {
          val playlistTrack = alicePlaylistTracks(trackIndex)
          tracks(trackIndex) must have(
            'name (playlistTrack.name.toString),
            'identifier (playlistTrack.identifier)
          )
          tracks(trackIndex).album must have('name (playlistTrack.album.name.toString))
          tracks(trackIndex).album.artists must have size playlistTrack.album.artists.size
          for (artistIndex ← tracks(trackIndex).album.artists.indices) {
            tracks(trackIndex).album.artists(artistIndex) must have(
              'name (playlistTrack.album.artists(artistIndex).name.toString)
            )
          }
          tracks(trackIndex).album.images must have size playlistTrack.album.images.size
          for (imageIndex ← tracks(trackIndex).album.images.indices) {
            tracks(trackIndex).album.images(imageIndex) must have(
              'url (playlistTrack.album.images(imageIndex).url),
              'width (playlistTrack.album.images(imageIndex).width),
              'height (playlistTrack.album.images(imageIndex).height)
            )
          }
        }
      }
    }

    "update queue current track" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = queueService.retrieve(aliceVenueDb.id).await()

      inside(queue.currentTrack) { case Some(track) ⇒
        track must have(
          'name (alicePlaylistTracks.head.name.toString),
          'identifier (alicePlaylistTracks.head.identifier)
        )
      }
    }

    "update queue items" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = queueService.retrieve(aliceVenueDb.id).await()

      for (item ← queue.items) item must have ('itemType (QueueItemType.Venue))
      queue.items.map(_.track) must contain theSameElementsAs queue.currentPlaylist.get.tracks
    }

    "update queue position" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = queueService.retrieve(aliceVenueDb.id).await()

      queue.position mustEqual Nil
    }

    "fail" when {
      "invalid identifier" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          controller.setCurrentPlaylist()(
            playlistRequest("wrong_identifier").withAliceToken).await()
        }
      }
      "invalid content provider" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          controller.setCurrentPlaylist()(
            playlistRequest("wrong_provider:wrong_identifier").withAliceToken).await()
        }
      }
    }
  }
}
