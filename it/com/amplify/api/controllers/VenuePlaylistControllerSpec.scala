package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models._
import com.amplify.api.exceptions.{InvalidProviderIdentifier, UnexpectedResponse}
import com.amplify.api.it.fixtures.{DbCoinFixture, DbVenueFixture}
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import com.amplify.api.services.external.spotify.Converters.toModelPlaylist
import org.mockito.Mockito.when
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.Future

class VenuePlaylistControllerSpec extends BaseIntegrationSpec with VenueRequests with Inside {

  val controller = instanceOf[VenuePlaylistController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueUid"
  val commandProcessor = app.actorSystem.actorSelection(path)

  class RetrievePlaylistsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "retrievePlaylists" should {
    "respond OK" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustEqual OK
    }
    "respond with playlists" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).head
      (jsonResponse \ "name").as[String] mustEqual alicePlaylist.name.toString
      (jsonResponse \ "identifier").as[String] mustEqual alicePlaylistUri.toString
      val image = (jsonResponse \ "images").head
      (image \ "url").as[String] mustEqual alicePlaylistImages.head.url
      (image \ "height").as[Int] mustEqual alicePlaylistImages.head.height.get
      (image \ "width").as[Int] mustEqual alicePlaylistImages.head.width.get
    }

    "fail" when {
      "Spotify responds with unexpected response" in new RetrievePlaylistsFixture {
        when(spotifyContentProvider.fetchPlaylists(aliceAccessToken))
          .thenReturn(Future.failed(UnexpectedResponse("Testing!")))

        intercept[UnexpectedResponse] {
          await(controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession))
        }
      }
    }
  }

  class RetrieveVenueCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  "retrieveVenueCurrentPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveVenueCurrentPlaylistFixture {
        val response =
          controller.retrieveVenueCurrentPlaylist(aliceVenueUid)(FakeRequest().withValidCoin)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveVenueCurrentPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(currentPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response =
        controller.retrieveVenueCurrentPlaylist(aliceVenueUid)(FakeRequest().withValidCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
    }
  }

  class RetrieveCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "retrieveCurrentPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveCurrentPlaylistFixture {
        val response =
          controller.retrieveCurrentPlaylist()(FakeRequest().withBody(()).withAliceSession)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveCurrentPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(currentPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response =
        controller.retrieveCurrentPlaylist()(FakeRequest().withBody(()).withAliceSession)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
    }
  }

  class SetCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "setCurrentPlaylist" should {
    "respond No content" in new SetCurrentPlaylistFixture {
      val response = controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession)
      status(response) mustEqual NO_CONTENT
    }
    "update queue current playlist" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      inside(queue.currentPlaylist) { case Some(Playlist(playlistInfo, tracks)) ⇒
        playlistInfo must have(
          'name (alicePlaylist.name),
          'identifier (alicePlaylistUri)
        )
        playlistInfo.images must have size alicePlaylist.images.size
        for (i ← playlistInfo.images.indices) {
          val playlistImage = alicePlaylist.images(i)
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
            'name (playlistTrack.track.name),
            'identifier (TrackUri(playlistTrack.track.id))
          )
          tracks(trackIndex).album must have('name (playlistTrack.track.album.name))
          tracks(trackIndex).album.artists must have size playlistTrack.track.album.artists.size
          for (artistIndex ← tracks(trackIndex).album.artists.indices) {
            tracks(trackIndex).album.artists(artistIndex) must have(
              'name (playlistTrack.track.album.artists(artistIndex).name)
            )
          }
          tracks(trackIndex).album.images must have size playlistTrack.track.album.images.size
          for (imageIndex ← tracks(trackIndex).album.images.indices) {
            tracks(trackIndex).album.images(imageIndex) must have(
              'url (playlistTrack.track.album.images(imageIndex).url),
              'width (playlistTrack.track.album.images(imageIndex).width),
              'height (playlistTrack.track.album.images(imageIndex).height)
            )
          }
        }
      }
    }
    "update queue current track" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      inside(queue.currentItem) { case Some(QueueItem(track, itemType)) ⇒
        itemType mustEqual QueueItemType.Venue
        track must have(
          'name (alicePlaylistTracks.head.track.name.toString),
          'identifier (TrackUri(alicePlaylistTracks.head.track.id))
        )
      }
    }
    "update queue items" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      for (item ← queue.allItems) item must have ('itemType (QueueItemType.Venue))
      queue.allItems.map(_.track) must contain theSameElementsAs queue.currentPlaylist.get.tracks
    }

    "fail" when {
      "invalid identifier" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setCurrentPlaylist()(
            playlistRequest("wrong_identifier").withAliceSession))
        }
      }
      "invalid content provider" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setCurrentPlaylist()(
            playlistRequest("wrong_provider:wrong_identifier").withAliceSession))
        }
      }
    }
  }
}
