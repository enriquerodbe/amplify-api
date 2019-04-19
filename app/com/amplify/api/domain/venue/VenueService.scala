package com.amplify.api.domain.venue

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Venue, VenueData}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieveOrCreate(venueData: VenueData): Future[Venue]

  def retrieve(uid: Uid): Future[Option[Venue]]
}
