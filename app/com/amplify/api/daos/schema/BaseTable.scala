package com.amplify.api.daos.schema

import com.amplify.api.domain.models.AuthProviderType
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives._
import java.sql.Timestamp
import java.time.Instant
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

// scalastyle:off public.methods.have.type
trait BaseTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit val instantType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)

  implicit val idType = MappedColumnType.base[Id, Long](_.value, Id.apply)

  implicit val nameType =
    MappedColumnType.base[Name, String](_.value, Name.apply)

  implicit val tokenType =
    MappedColumnType.base[Token, String](_.value, Token.apply)

  implicit val identifierType =
    MappedColumnType.base[Identifier, String](_.value, Identifier.apply)

  implicit val uidType = MappedColumnType.base[Uid, String](_.value, Uid.apply)

  implicit val authProviderTypeType =
    MappedColumnType.base[AuthProviderType, Int](_.id, AuthProviderType.apply)
}
