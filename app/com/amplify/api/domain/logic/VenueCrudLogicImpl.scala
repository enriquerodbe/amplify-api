package com.amplify.api.domain.logic
import com.amplify.api.domain.models.Venue
import com.amplify.api.services.VenueService
import javax.inject.Inject
import scala.concurrent.Future

class VenueCrudLogicImpl @Inject()(venueService: VenueService) extends VenueCrudLogic {

  override def listAll: Future[Seq[Venue]] = venueService.listAll
}
