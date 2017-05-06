package com.amplify.api.daos.schema

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

// scalastyle:off public.methods.have.type
trait BaseTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit val nameType =
    MappedColumnType.base[Name, String](_.value, Name.apply)

  implicit val emailType =
    MappedColumnType.base[Email, String](_.value, Email.apply)

  implicit val identifierType =
    MappedColumnType.base[Identifier, String](_.value, Identifier.apply)

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
      maybeProviderIdentifier: Option[ContentProviderIdentifier])
  : Option[(Option[ContentProviderType], Option[Identifier])] = {
    maybeProviderIdentifier match {
      case Some(providerIdentifier) ⇒
        Some(Some(providerIdentifier.contentProvider), Some(providerIdentifier.identifier))
      case None ⇒
        Some((None, None))
    }
  }
}
