package com.amplify.api.it

import com.amplify.api.controllers.dtos.Coin.CreateCoinsRequest
import com.amplify.api.controllers.dtos.Playlist.PlaylistRequest
import com.amplify.api.controllers.dtos.Venue.VenueSignInRequest
import com.amplify.api.domain.models.primitives.{AuthorizationCode, Token}
import play.api.test.FakeRequest

trait VenueRequests {

  def venueRequest(code: Token[AuthorizationCode]): FakeRequest[VenueSignInRequest] = {
    FakeRequest().withBody(VenueSignInRequest(code.value))
  }

  def playlistRequest(identifier: String): FakeRequest[PlaylistRequest] = {
    FakeRequest().withBody(PlaylistRequest(identifier))
  }

  def createCoinsRequest(number: Int): FakeRequest[CreateCoinsRequest] = {
    FakeRequest().withBody(CreateCoinsRequest(number))
  }
}
