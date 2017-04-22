package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{Playlist, User}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(authToken: String)(implicit user: User): Future[Seq[Playlist]]
}
