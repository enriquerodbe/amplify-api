package com.amplify.api.services

import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist}
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def create(
      userData: UserData,
      name: Name): Future[Unit]

  def retrievePlaylists(userReq: AuthenticatedUserReq): Future[Seq[Playlist]]
}
