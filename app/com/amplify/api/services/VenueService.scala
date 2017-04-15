package com.amplify.api.services

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def create(
      name: Name[Venue],
      userData: UserData,
      authProviderType: AuthProviderType): Future[Unit]

  def listAll: Future[Seq[Venue]]
}
