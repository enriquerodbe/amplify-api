package com.amplify.api.it

import com.amplify.api.domain.models.primitives.{AuthorizationCode, Token}
import com.amplify.api.shared.controllers.dtos.CoinDtos.CreateCoinsRequest
import com.amplify.api.shared.controllers.dtos.PlaylistDtos.PlaylistRequest
import com.amplify.api.shared.controllers.dtos.VenueDtos.VenueSignInRequest
import play.api.test.FakeRequest

trait VenueRequests {

  def venueRequest(code: Token[AuthorizationCode]): FakeRequest[VenueSignInRequest] = {
    FakeRequest().withBody(VenueSignInRequest(code.value))
  }

  def createCoinsRequest(number: Int): FakeRequest[CreateCoinsRequest] = {
    FakeRequest().withBody(CreateCoinsRequest(number))
  }

  def playlistRequest(identifier: String): FakeRequest[PlaylistRequest] = {
    FakeRequest().withBody(PlaylistRequest(identifier))
  }
}
