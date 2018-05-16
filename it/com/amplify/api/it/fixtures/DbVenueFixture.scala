package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.AuthProviderType.Spotify
import com.amplify.api.domain.models.primitives.{Id, Name}

trait DbVenueFixture extends BaseDbFixture with CommonData with VenuesTable {

  import profile.api._

  val aliceDbVenue =
    DbVenue(
      aliceDbVenueId,
      "Alice's Bar",
      aliceVenueUid,
      Spotify → aliceSpotifyId,
      aliceRefreshToken,
      aliceAccessToken)

  def insertVenue(venue: DbVenue): DbVenue = await(db.run(insertVenuesQuery += venue))
  def findVenues(name: String): Seq[DbVenue] = {
    await(db.run(venuesTable.filter(_.name === Name(name)).result))
  }
  def getVenue(id: Id): DbVenue = {
    await(db.run(venuesTable.filter(_.id === id).result.head))
  }

  insertVenue(aliceDbVenue)
}
