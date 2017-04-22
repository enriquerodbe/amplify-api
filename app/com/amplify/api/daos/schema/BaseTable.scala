package com.amplify.api.daos.schema

import com.amplify.api.domain.models.ContentProviderType
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

// scalastyle:off public.methods.have.type
trait BaseTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit def nameType[T] =
    MappedColumnType.base[Name[T], String](_.value, Name.apply)

  implicit val emailType =
    MappedColumnType.base[Email, String](_.value, Email.apply)

  implicit def identifierType[T] =
    MappedColumnType.base[Identifier[T], String](_.value, Identifier.apply)

  implicit val authProviderTypeType =
    MappedColumnType.base[ContentProviderType, Int](_.id, ContentProviderType.apply)
}
