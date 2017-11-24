package com.amplify.api.aggregates.queue.services.external.firebase

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Reads}

object Dtos {

  case class SendNotificationResponse(name: Option[String])

  implicit val sendNotificationResponseReads: Reads[SendNotificationResponse] = {
    JsonNaming.snakecase(Json.reads[SendNotificationResponse])
  }
}
