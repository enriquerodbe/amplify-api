package com.amplify.api.it

import com.amplify.api.controllers.dtos.Queue.AddTrackRequest
import com.amplify.api.domain.models.TrackIdentifier
import play.api.test.FakeRequest

trait UserRequests {

  def fakeRequest(): FakeRequest[Unit] = FakeRequest().withBody(())

  def addTrackRequest(identifier: TrackIdentifier): FakeRequest[AddTrackRequest] = {
    FakeRequest().withBody(AddTrackRequest(identifier.toString))
  }
}
