package com.amplify.api.controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.controllers.dtos.Venue.{PlaylistRequest, playlistToPlaylistResponse}
import com.amplify.api.domain.logic.VenueCrudLogic
import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.utils.AuthenticatedRequests
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.{ExecutionContext, Future}
import scala.language.reflectiveCalls
import scala.util.{Failure, Success}

// scalastyle:off public.methods.have.type
class VenueCrudController @Inject()(
    venueCrudLogic: VenueCrudLogic,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext) extends Controller with AuthenticatedRequests {

  def fetchPlaylists(offset: Int, limit: Int) = authenticated() { request ⇒
    val eventualPlaylists = venueCrudLogic.retrievePlaylists(request.subject.userReq)
    eventualPlaylists.map(playlists ⇒ Ok(Json.toJson(playlists.map(playlistToPlaylistResponse))))
  }

  def setCurrentPlaylist() = authenticated(parse.json[PlaylistRequest]) { request ⇒
    ContentProviderIdentifier.fromString(request.body.identifier) match {
      case Success(identifier) ⇒
        venueCrudLogic.setCurrentPlaylist(request.subject.userReq, identifier).map(_ ⇒ NoContent)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
