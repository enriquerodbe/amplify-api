package com.amplify.api.domain.venue

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.{Access, Refresh, Token, _}
import com.amplify.api.shared.daos.BaseTable

trait VenuesTable extends BaseTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class Venues(tag: Tag) extends Table[DbVenue](tag, "venues") {
    def name = column[Name]("name")
    def uid = column[Uid]("uid")
    def authProviderType = column[AuthProviderType]("auth_provider")
    def authIdentifier = column[Identifier]("auth_identifier")
    def refreshToken = column[Token[Refresh]]("refresh_token")
    def accessToken = column[Token[Access]]("access_token")

    def authProviderIdentifier =
      (authProviderType, authIdentifier) <>
        ((AuthProviderIdentifier.apply _).tupled, AuthProviderIdentifier.unapply)

    def * =
      (name, uid, authProviderIdentifier, refreshToken, accessToken) <>
        (DbVenue.tupled, DbVenue.unapply)
  }

  lazy val venuesTable = TableQuery[Venues]
  lazy val insertVenuesQuery = venuesTable.returning(venuesTable)
}
