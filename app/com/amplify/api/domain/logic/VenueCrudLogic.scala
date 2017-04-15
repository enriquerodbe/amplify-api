package com.amplify.api.domain.logic

import com.amplify.api.domain.models.Venue
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def listAll: Future[Seq[Venue]]
}
