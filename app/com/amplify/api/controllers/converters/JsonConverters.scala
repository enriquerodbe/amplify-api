package com.amplify.api.controllers.converters

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType, Playlist}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Identifier, Name}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonConverters {

  implicit val nameReads: Reads[Name] = Reads {
    case JsString(value) ⇒ JsSuccess(value)
    case other ⇒ JsError(s"Name must be a string but found: $other")
  }

  implicit val nameWrites: Writes[Name] = Writes(JsString(_))

  implicit val nameFormat: Format[Name] = Format(nameReads, nameWrites)

  implicit val identifierReads: Reads[Identifier] = Reads {
    case JsString(value) ⇒ JsSuccess(value)
    case other ⇒ JsError(s"Identifier must be a string but found: $other")
  }

  implicit val identifierWrites: Writes[Identifier] = Writes(JsString(_))

  implicit val identifierFormat: Format[Identifier] = {
    Format(identifierReads, identifierWrites)
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

  implicit def contentProviderIdentifierFormat: Format[ContentProviderIdentifier] = {
    ((__ \ "content_provider").format[ContentProviderType] ~
      (__ \ "identifier").format[Identifier]
      )(ContentProviderIdentifier.apply, unlift(ContentProviderIdentifier.unapply))
  }

  implicit val playlistFormat: Format[Playlist] = {
    ((__ \ "name").format[Name] ~
      (__ \ "identifier").format[ContentProviderIdentifier]
      )(Playlist.apply, unlift(Playlist.unapply))
  }
}
