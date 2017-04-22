package com.amplify.api.controllers.converters

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType, Playlist}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Identifier, Name}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonConverters {

  implicit def nameReads[T]: Reads[Name[T]] = Reads {
    case JsString(value) ⇒ JsSuccess(value)
    case other ⇒ JsError(s"Name must be a string but found: $other")
  }

  implicit def nameWrites[T]: Writes[Name[T]] = Writes(JsString(_))

  implicit def nameFormat[T]: Format[Name[T]] = Format(nameReads, nameWrites)

  implicit def identifierReads[T]: Reads[Identifier[T]] = Reads {
    case JsString(value) ⇒ JsSuccess(value)
    case other ⇒ JsError(s"Identifier must be a string but found: $other")
  }

  implicit def identifierWrites[T]: Writes[Identifier[T]] = Writes(JsString(_))

  implicit def identifierFormat[T]: Format[Identifier[T]] = {
    Format(identifierReads[T], identifierWrites[T])
  }

  implicit val contentProviderReads: Reads[ContentProviderType] = Reads {
    case JsString(value) ⇒ ContentProviderType.find(value) match {
      case Some(contentProvider) ⇒ JsSuccess(contentProvider)
      case _ ⇒ JsError(s"Invalid content provider: $value")
    }
    case other ⇒ JsError(s"Content provider must be a string but found $other")
  }

  implicit val contentProviderWrites: Writes[ContentProviderType] = {
    Writes(contentProvider ⇒ JsString(contentProvider.toString))
  }

  implicit val contentProviderFormat: Format[ContentProviderType] = {
    Format(contentProviderReads, contentProviderWrites)
  }

  implicit def contentProviderIdentifierFormat[T]: Format[ContentProviderIdentifier[T]] = {
    ((__ \ "content_provider").format[ContentProviderType] ~
      (__ \ "identifier").format[Identifier[T]]
      )(ContentProviderIdentifier.apply[T], unlift(ContentProviderIdentifier.unapply))
  }

  implicit val playlistFormat: Format[Playlist] = {
    ((__ \ "name").format[Name[Playlist]] ~
      (__ \ "identifier").format[ContentProviderIdentifier[Playlist]]
      )(Playlist.apply, unlift(Playlist.unapply))
  }
}
