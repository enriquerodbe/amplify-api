package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, Venue}
import com.amplify.api.domain.models.primitives.Name
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signUp(authToken: AuthToken, name: Name): Future[Venue]
}
