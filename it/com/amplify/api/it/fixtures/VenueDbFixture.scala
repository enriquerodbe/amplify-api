package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.{UsersTable, VenuesTable}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{User, Venue}

trait VenueDbFixture extends BaseDbFixture with CommonData with VenuesTable with UsersTable {

  import profile.api._

  val aliceUserDb = UserDb(aliceUserDbId, "Alice Cooper", Spotify → aliceSpotifyId)
  val aliceVenueDb = VenueDb(aliceVenueDbId, "Alice's Bar", aliceUserDb.id, "Fa84A3fl")
  val bobUserDb = UserDb(bobUserDbId, "Bob Marley", Spotify → bobSpotifyId)

  def insertUser(user: UserDb): Id[User] = {
    db.run(usersTable returning usersTable.map(_.id) += user).await()
  }
  def insertVenue(venue: VenueDb): Id[Venue] = {
    db.run(venuesTable returning venuesTable.map(_.id) += venue).await()
  }

  def findUsers(name: String): Seq[UserDb] = {
    db.run(usersTable.filter(_.name === Name(name)).result).await()
  }
  def findVenues(name: String): Seq[VenueDb] = {
    db.run(venuesTable.filter(_.name === Name(name)).result).await()
  }

  insertUser(aliceUserDb)
  insertVenue(aliceVenueDb)
}
