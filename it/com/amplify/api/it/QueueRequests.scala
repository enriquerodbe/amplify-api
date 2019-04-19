package com.amplify.api.it

import com.amplify.api.shared.controllers.dtos.PlaylistDtos.PlaylistRequest
import play.api.test.FakeRequest

trait QueueRequests {

  def playlistRequest(identifier: String): FakeRequest[PlaylistRequest] = {
    FakeRequest().withBody(PlaylistRequest(identifier))
  }
}
