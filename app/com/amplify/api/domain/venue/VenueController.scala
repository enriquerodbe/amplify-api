package com.amplify.api.domain.venue

import be.objectify.deadbolt.scala.ActionBuilders
import com.amplify.api.domain.coin.CoinAuthRequests
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.venue.auth.VenueAuthRequests
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.playlistInfoToPlaylistInfoResponse
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext

// scalastyle:off public.methods.have.type
class VenueController @Inject()(
    cc: ControllerComponents,
    playlistService: PlaylistService,
    val actionBuilder: ActionBuilders)(
    implicit ec: ExecutionContext)
  extends AbstractController(cc) with VenueAuthRequests with CoinAuthRequests {

  def retrievePlaylists() = authenticatedVenue(parse.empty) { request ⇒
    val eventualPlaylists = playlistService.retrievePlaylists(request.subject.venue.uid)
    eventualPlaylists.map(_.map(playlistInfoToPlaylistInfoResponse))
  }
}
