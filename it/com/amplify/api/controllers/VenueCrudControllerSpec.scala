package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.command_processors.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.command_processors.queue.{CommandType, EventType}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.{ContentProviderIdentifier, Playlist, Queue, QueueItemType}
import com.amplify.api.exceptions.{InvalidProviderIdentifier, UnexpectedResponse}
import com.amplify.api.it.fixtures.{QueueCommandDbFixture, QueueEventDbFixture, SpotifyContext, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import org.mockito.Mockito.when
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class VenueCrudControllerSpec
  extends BaseIntegrationSpec with SpotifyContext with VenueRequests with Inside {

  val controller = instanceOf[VenueCrudController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueDbId"
  val queueService = app.actorSystem.actorSelection(path)

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
    extends VenueDbFixture with QueueCommandDbFixture with QueueEventDbFixture

  "setCurrentPlaylist" should {
    "respond No content" in new SetCurrentPlaylistFixture {
      val response = controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken)

      status(response) mustEqual NO_CONTENT
    }
    "create queue command" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDbId)

      queueCommands must have size 1
      queueCommands.head must have(
        'queueCommandType (CommandType.SetCurrentPlaylist),
        'contentIdentifier (Some(ContentProviderIdentifier(Spotify, alicePlaylistIdentifier)))
      )
    }
    "create queue events" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDbId)
      val queueEvents = findQueueEvents(queueCommands.head.id)

      queueEvents must have size 4
      queueEvents(0) must have ('queueEventType (EventType.VenueTracksRemoved))
      queueEvents(1) must have(
        'queueEventType (EventType.VenueTrackAdded),
        'contentIdentifier (Some(poisonTrackData.identifier))
      )
      queueEvents(2) must have(
        'queueEventType (EventType.VenueTrackAdded),
        'contentIdentifier (Some(bedOfNailsTrackData.identifier))
      )
      queueEvents(3) must have(
        'queueEventType (EventType.CurrentPlaylistSet),
        'contentIdentifier (Some(ContentProviderIdentifier(Spotify, alicePlaylistIdentifier)))
      )
    }
    "update queue current playlist" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = (queueService ? RetrieveMaterialized).mapTo[Queue].await()

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

      val queue = (queueService ? RetrieveMaterialized).mapTo[Queue].await()

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

      val queue = (queueService ? RetrieveMaterialized).mapTo[Queue].await()

      for (item ← queue.items) item must have ('itemType (QueueItemType.Venue))
      queue.items.map(_.track) must contain theSameElementsAs queue.currentPlaylist.get.tracks
    }

    "update queue position" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier).withAliceToken).await()

      val queue = (queueService ? RetrieveMaterialized).mapTo[Queue].await()

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

  class RetrieveQueueFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture

  "retrieveQueue" should {
    "respond OK" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(FakeRequest().withAliceToken)

      status(response) mustBe OK
    }

    "respond with queue" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(FakeRequest().withAliceToken)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)

      (jsonResponse \ "current_playlist").as[String] mustEqual alicePlaylistData.identifier.toString
      val currentTrack = jsonResponse \ "current_track"
      (currentTrack \ "name").as[String] mustEqual poisonTrackData.name.toString
      (currentTrack \ "content_provider").as[String] mustEqual Spotify.toString
      ((currentTrack \ "content_identifier").as[String]
        mustEqual poisonTrackData.identifier.identifier.toString)
      val album = currentTrack \ "album"
      (album \ "name").as[String] mustEqual trashAlbumData.name.toString
      (((album \ "artists")(0) \ "name").as[String]
        mustEqual trashAlbumData.artists.head.name.toString)
      (album \ "images").as[JsArray].value mustBe empty
    }
  }

  class RetrieveCurrentFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture

  "retrieveCurrent" should {
    "respond OK" in new RetrieveCurrentFixture {
      val response = controller.retrieveCurrent()(FakeRequest().withAliceToken)

      status(response) mustBe OK
    }
    "respond with venue" in new RetrieveCurrentFixture {
      val response = controller.retrieveCurrent()(FakeRequest().withAliceToken)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "name").as[String] mustEqual aliceVenueDb.name.toString
      (jsonResponse \ "uid").as[String] must have size 8
    }
  }

  class RetrieveAllFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture

  "retrieveAll" should {
    "respond OK" in new RetrieveAllFixture {
      val response = controller.retrieveAll()(FakeRequest().withAliceToken)

      status(response) mustBe OK
    }
    "respond with venues" in new RetrieveAllFixture {
      val response = controller.retrieveAll()(FakeRequest().withAliceToken)

      contentType(response) must contain (Http.MimeTypes.JSON)
      inside(contentAsJson(response)) { case JsArray(Seq(venue)) ⇒
        (venue \ "name").as[String] mustEqual aliceVenueDb.name.toString
        (venue \ "uid").as[String] must have size 8
      }
    }
  }
}
