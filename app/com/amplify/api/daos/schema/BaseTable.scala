package com.amplify.api.daos.schema

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives._
import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
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
    MappedColumnType.base[ContentProviderType, Int](_.id, ContentProviderType.apply)

  def mapOptionalProviderIdentifier(
      values: (Option[ContentProviderType],
        Option[Identifier])): Option[ContentProviderIdentifier] = {
    values match {
      case (Some(providerType), Some(identifier)) ⇒
        Some(ContentProviderIdentifier(providerType, identifier))
      case _ ⇒
        None
    }
  }

  def unmapOptionalProviderIdentifier(
      maybeProviderIdentifier: Option[ContentProviderIdentifier]
  ) : Option[(Option[ContentProviderType], Option[Identifier])] = {
    maybeProviderIdentifier match {
      case Some(providerIdentifier) ⇒
        Some(Some(providerIdentifier.contentProvider), Some(providerIdentifier.identifier))
      case None ⇒
        Some((None, None))
    }
  }
}
