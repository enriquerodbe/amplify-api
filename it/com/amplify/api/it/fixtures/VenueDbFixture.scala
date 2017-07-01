package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.schema.{UsersTable, VenuesTable}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import play.api.db.slick.DatabaseConfigProvider

class VenueDbFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
  extends BaseDbFixture with VenuesTable with UsersTable {

  val userDb = UserDb(name = "Enrique", authIdentifier = Spotify → "id")
}
