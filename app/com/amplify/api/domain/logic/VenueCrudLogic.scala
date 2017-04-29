package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthenticatedUserReq, Playlist}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]]
}
