package com.amplify.api.it

import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.shared.controllers.dtos.QueueDtos.AddTrackRequest
import play.api.test.FakeRequest

trait CoinRequests {

  def addTrackRequest(venueUid: Uid, identifier: TrackIdentifier): FakeRequest[AddTrackRequest] = {
    FakeRequest().withBody(AddTrackRequest(venueUid.value, identifier.toString))
  }
}
