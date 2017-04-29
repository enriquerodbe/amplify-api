package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist}
import com.amplify.api.services.VenueService
import javax.inject.Inject
import scala.concurrent.Future

class VenueCrudLogicImpl @Inject()(venueService: VenueService) extends VenueCrudLogic {

  override def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    venueService.retrievePlaylists(user)
  }
}
