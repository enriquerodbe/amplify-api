package com.amplify.api.it

import com.amplify.api.controllers.dtos.Coin.CreateCoinsRequest
import com.amplify.api.controllers.dtos.Playlist.PlaylistRequest
import com.amplify.api.controllers.dtos.Venue.VenueRequest
import play.api.test.FakeRequest

trait VenueRequests {

  def venueRequest(name: String): FakeRequest[VenueRequest] = {
    FakeRequest().withBody(VenueRequest(name))
  }

  def playlistRequest(identifier: String): FakeRequest[PlaylistRequest] = {
    FakeRequest().withBody(PlaylistRequest(identifier))
  }

  def createCoinsRequest(number: Int): FakeRequest[CreateCoinsRequest] = {
    FakeRequest().withBody(CreateCoinsRequest(number))
  }
}
