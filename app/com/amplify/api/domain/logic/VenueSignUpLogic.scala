package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueSignUpLogicImpl])
trait VenueSignUpLogic {

  def signUp(name: Name[Venue], authProviderType: AuthProviderType, authToken: String): Future[Unit]
}
