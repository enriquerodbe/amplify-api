package com.amplify.api.domain.venue

import com.amplify.api.domain.models.AuthProviderType.Spotify
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.it.fixtures.{BaseDbFixture, CommonData}

trait DbVenueFixture extends BaseDbFixture with CommonData with VenuesTable {

  import profile.api._

  val aliceDbVenue =
    DbVenue(
      "Alice's Bar",
      aliceVenueUid,
      Spotify → aliceSpotifyId,
      aliceRefreshToken,
      aliceAccessToken)

  def insertVenue(venue: DbVenue): DbVenue = await(db.run(insertVenuesQuery += venue))
  def findVenues(name: String): Seq[DbVenue] = {
    await(db.run(venuesTable.filter(_.name === Name(name)).result))
  }
  def getVenue(uid: Uid): DbVenue = {
    await(db.run(venuesTable.filter(_.uid === uid).result.head))
  }

  insertVenue(aliceDbVenue)
}
