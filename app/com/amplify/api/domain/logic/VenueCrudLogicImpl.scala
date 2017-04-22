package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{Playlist, User}
import com.amplify.api.services.VenueService
import javax.inject.Inject
import scala.concurrent.Future

class VenueCrudLogicImpl @Inject()(venueService: VenueService) extends VenueCrudLogic {

  override def retrievePlaylists(authToken: String)(implicit user: User): Future[Seq[Playlist]] = {
    venueService.retrievePlaylists(authToken)
  }
}
