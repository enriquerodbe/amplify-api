package com.amplify.api.daos.schema

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives._
import com.amplify.api.domain.models.{AuthProviderIdentifier, AuthProviderType, ContentProviderIdentifier, ContentProviderType}
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

  implicit val contentProviderTypeType =
    MappedColumnType.base[ContentProviderType, Int](_.id, ContentProviderType.apply)

  def mapOptionalContentProviderIdentifier(
      values: (Option[ContentProviderType],
        Option[Identifier])): Option[ContentProviderIdentifier] = {
    values match {
      case (Some(providerType), Some(identifier)) ⇒
        Some(ContentProviderIdentifier(providerType, identifier))
      case _ ⇒
        None
    }
  }

  def unmapOptionalContentProviderIdentifier(
      maybeProviderIdentifier: Option[ContentProviderIdentifier]
  ) : Option[(Option[ContentProviderType], Option[Identifier])] = {
    maybeProviderIdentifier match {
      case Some(providerIdentifier) ⇒
        Some(Some(providerIdentifier.contentProvider), Some(providerIdentifier.identifier))
      case None ⇒
        Some((None, None))
    }
  }

  def mapOptionalAuthProviderIdentifier(
      values: (Option[AuthProviderType], Option[Identifier])): Option[AuthProviderIdentifier] = {
    values match {
      case (Some(providerType), Some(identifier)) ⇒
        Some(AuthProviderIdentifier(providerType, identifier))
      case _ ⇒
        None
    }
  }

  def unmapOptionalAuthProviderIdentifier(
      maybeProviderIdentifier: Option[AuthProviderIdentifier]
  ) : Option[(Option[AuthProviderType], Option[Identifier])] = {
    maybeProviderIdentifier match {
      case Some(providerIdentifier) ⇒
        Some(Some(providerIdentifier.authProvider), Some(providerIdentifier.identifier))
      case None ⇒
        Some((None, None))
    }
  }
}
