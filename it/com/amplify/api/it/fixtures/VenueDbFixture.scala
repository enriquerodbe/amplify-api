package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.{UsersTable, VenuesTable}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.{User, Venue}
import play.api.db.slick.DatabaseConfigProvider

class VenueDbFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
  extends BaseDbFixture with VenuesTable with UsersTable {

  import profile.api._

  val aliceUserDb =
    UserDb(id = 1L, name = "Alice Cooper", authIdentifier = Spotify → "alice-spotify-id")
  val aliceVenueDb = VenueDb(name = "Alice's Bar", userId = aliceUserDb.id, uid = "Fa84A3fl")
  val bobUserDb = UserDb(name = "Bob Marley", authIdentifier = Spotify → "bob-spotify-id")

  def insertUser(user: UserDb): Id[User] = {
    db.run(usersTable returning usersTable.map(_.id) += user).await()
  }
  def insertVenue(venue: VenueDb): Id[Venue] = {
    db.run(venuesTable returning venuesTable.map(_.id) += venue).await()
  }

  insertUser(aliceUserDb)
  insertVenue(aliceVenueDb)
}
