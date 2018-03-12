package com.amplify.api.domain.models.primitives

import com.amplify.api.domain.models.{AuthProviderIdentifier, ContentProviderIdentifier}
import play.api.libs.json.{JsError, JsString, JsSuccess, Reads}
import scala.util.{Failure, Success}

object PrimitivesSerializer {

  implicit val uidReads: Reads[Uid] = Reads {
    case JsString(string) ⇒ JsSuccess(Uid(string))
    case other ⇒ JsError(s"Expected JsString for Uid, got: $other")
  }

  implicit val contentProviderIdentifierReads: Reads[ContentProviderIdentifier] = Reads {
    case JsString(string) ⇒
      ContentProviderIdentifier.fromString(string) match {
        case Success(identifier) ⇒ JsSuccess(identifier)
        case Failure(ex) ⇒ JsError(ex.getMessage)
      }
    case other ⇒
      JsError(s"Expected JsString for ContentProviderIdentifier, got: $other")
  }

  implicit val authProviderIdentifierReads: Reads[AuthProviderIdentifier] = Reads {
    case JsString(string) ⇒
      AuthProviderIdentifier.fromString(string) match {
        case Success(identifier) ⇒ JsSuccess(identifier)
        case Failure(ex) ⇒ JsError(ex.getMessage)
      }
    case other ⇒
      JsError(s"Expected JsString for AuthProviderIdentifier, got: $other")
  }
}
