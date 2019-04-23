package com.amplify.api.shared.daos

import com.amplify.api.domain.models.{AuthProviderType, ContentIdentifier}
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

  implicit val identifierType =
    MappedColumnType.base[Identifier, String](_.value, Identifier.apply)

  implicit val uidType = MappedColumnType.base[Uid, String](_.value, Uid.apply)

  implicit val authProviderTypeType =
    MappedColumnType.base[AuthProviderType, Int](_.id, AuthProviderType.apply)

  implicit def tokenType[T <: TokenType] = {
    MappedColumnType.base[Token[T], String](_.value, Token.apply[T])
  }

  implicit val contentIdentifierType = {
    MappedColumnType.base[ContentIdentifier, String](
      _.toString,
      ContentIdentifier.fromString(_).get)
  }

  implicit val codeType = MappedColumnType.base[Code, String](_.value, Code.apply)
}
