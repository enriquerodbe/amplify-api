package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.AuthProviderType.Spotify
import com.amplify.api.domain.models.primitives.{Id, Name}

trait VenueDbFixture extends BaseDbFixture with CommonData with VenuesTable {

  import profile.api._

  val aliceVenueDb =
    VenueDb(aliceVenueDbId, "Alice's Bar", aliceVenueUid, Spotify → aliceSpotifyId)

  def insertVenue(venue: VenueDb): Id = {
    db.run(venuesTable returning venuesTable.map(_.id) += venue).await()
  }
  def findVenues(name: String): Seq[VenueDb] = {
    db.run(venuesTable.filter(_.name === Name(name)).result).await()
  }
  def getVenue(id: Id): VenueDb = {
    db.run(venuesTable.filter(_.id === id).result.head).await()
  }

  insertVenue(aliceVenueDb)
}
