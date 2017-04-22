package com.amplify.api.domain.logic

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signUp(
      contentProvider: ContentProviderType,
      authToken: String,
      name: Name[Venue]): Future[Unit]
}
