package com.amplify.api.controllers.dtos

import com.amplify.api.controllers.dtos.User.{UserResponse, authenticatedUserToUserResponse}
import com.amplify.api.domain.models.{Playlist, AuthenticatedVenue}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads, Writes}

object Venue {

  case class SignUpReq(name: String)
  implicit val signUpReads: Reads[SignUpReq] = JsonNaming.snakecase(Json.reads[SignUpReq])

  case class VenueResponse(user: UserResponse, name: String)
  def venueToVenueResponse(venue: AuthenticatedVenue): VenueResponse = {
    VenueResponse(authenticatedUserToUserResponse(venue.user), venue.name)
  }
  implicit val venueResponseWrites: Writes[VenueResponse] = Json.writes[VenueResponse]

  case class PlaylistResponse(name: String, identifier: String)
  def playlistToPlaylistResponse(playlist: Playlist): PlaylistResponse = {
    PlaylistResponse(playlist.name, playlist.identifier)
  }
  implicit val playlistResponseWrites: Writes[PlaylistResponse] = Json.writes[PlaylistResponse]
}
