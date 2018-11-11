package com.amplify.api.domain.venue

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Venue, VenueData}
import com.amplify.api.domain.venue.VenueConverter.{dbVenueToVenue, venueDataToDbVenue}
import com.amplify.api.shared.daos.DbioRunner
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[Option[Venue]] = {
    db.run(venueDao.retrieve(uid).map(_.map(dbVenueToVenue)))
  }

  override def retrieveOrCreate(venueData: VenueData): Future[Venue] = {
    db.run(venueDao.retrieveOrCreate(venueDataToDbVenue(venueData))).map(dbVenueToVenue)
  }
}
