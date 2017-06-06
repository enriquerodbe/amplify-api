package com.amplify.api.daos.schema

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType, EventSourceType, QueueEventType}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.EventSourceType.EventSourceType
import com.amplify.api.domain.models.QueueEventType.QueueEventType
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name, Uid}
import java.sql.Timestamp
import java.time.Instant
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

// scalastyle:off public.methods.have.type
trait BaseTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit val instantType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)

  implicit val nameType =
    MappedColumnType.base[Name, String](_.value, Name.apply)

  implicit val emailType =
    MappedColumnType.base[Email, String](_.value, Email.apply)

  implicit val identifierType =
    MappedColumnType.base[Identifier, String](_.value, Identifier.apply)

  implicit val uidType = MappedColumnType.base[Uid, String](_.value, Uid.apply)

  implicit val authProviderTypeType =
    MappedColumnType.base[ContentProviderType, Int](_.id, ContentProviderType.apply)

  implicit val eventSourceTypeType =
    MappedColumnType.base[EventSourceType, Int](_.id, EventSourceType.apply)

  implicit val queueEventTypeType =
    MappedColumnType.base[QueueEventType, Int](_.id, QueueEventType.apply)

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
