package com.amplify.api.shared.controllers

import com.amplify.api.domain.venue.VenueController
import com.amplify.api.it.fixtures.DbVenueFixture
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import com.amplify.api.shared.exceptions.UnexpectedResponse
import org.mockito.Mockito.when
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.Future

class PlaylistControllerSpec extends BaseIntegrationSpec with VenueRequests with Inside {

  val controller = instanceOf[VenueController]
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
}
