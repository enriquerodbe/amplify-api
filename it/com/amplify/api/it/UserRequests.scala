package com.amplify.api.it

import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.shared.controllers.dtos.QueueDtos.AddTrackRequest
import play.api.test.FakeRequest

trait UserRequests {

  def fakeRequest(): FakeRequest[Unit] = FakeRequest().withBody(())

  def addTrackRequest(identifier: TrackIdentifier): FakeRequest[AddTrackRequest] = {
    FakeRequest().withBody(AddTrackRequest(identifier.toString))
  }
}
