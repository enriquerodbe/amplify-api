package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.{InvalidProviderIdentifier, UnexpectedResponse}
import com.amplify.api.it.fixtures.{SpotifyContext, VenueDbFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import org.mockito.Mockito.when
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsArray, JsDefined}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.Future

class VenueCrudControllerSpec
  extends BaseIntegrationSpec with SpotifyContext with VenueRequests with Inside {

  val controller = instanceOf[VenueCrudController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueUid"
  val commandProcessor = app.actorSystem.actorSelection(path)

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
      "Spotify responds with unexpected response" in new RetrievePlaylistsFixture {
        when(spotifyContentProvider.fetchPlaylists(aliceAuthToken))
          .thenReturn(Future.failed(UnexpectedResponse("Testing!")))

        intercept[UnexpectedResponse] {
          controller.retrievePlaylists()(FakeRequest().withAliceToken).await()
        }
      }
    }
  }

  class SetCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture

  "setCurrentPlaylist" should {
    "respond No content" in new SetCurrentPlaylistFixture {
      val response = controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier.toString).withAliceToken)

      status(response) mustEqual NO_CONTENT
    }
    "update queue current playlist" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier.toString).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

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
        playlistRequest(alicePlaylistData.identifier.toString).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      inside(queue.currentItem) { case Some(QueueItem(track, itemType)) ⇒
        itemType mustEqual QueueItemType.Venue
        track must have(
          'name (alicePlaylistTracks.head.name.toString),
          'identifier (alicePlaylistTracks.head.identifier)
        )
      }
    }
    "update queue items" in new SetCurrentPlaylistFixture {
      controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistData.identifier.toString).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveState).mapTo[Queue].await()

      for (item ← queue.allItems) item must have ('itemType (QueueItemType.Venue))
      queue.allItems.map(_.track) must contain theSameElementsAs queue.currentPlaylist.get.tracks
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

      (jsonResponse \ "tracks") mustEqual JsDefined(JsArray(Seq.empty))
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
