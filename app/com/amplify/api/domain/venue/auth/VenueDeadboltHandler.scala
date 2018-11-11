package com.amplify.api.domain.venue.auth

import be.objectify.deadbolt.scala.AuthenticatedRequest
import be.objectify.deadbolt.scala.models.Subject
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.venue.VenueSubject
import com.amplify.api.shared.controllers.auth.AbstractDeadboltHandler
import scala.concurrent.{ExecutionContext, Future}

class VenueDeadboltHandler(venueAuthLogic: VenueAuthLogic)(implicit ec: ExecutionContext)
  extends AbstractDeadboltHandler {

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    request
      .session
      .get(AuthHeaders.VENUE_UID)
      .map(Uid(_))
      .map(venueAuthLogic.login(_).map(_.map(VenueSubject(_))))
      .getOrElse(Future.successful(None))
  }
}
