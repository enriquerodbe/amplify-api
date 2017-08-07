package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Image ⇒ ModelImage}
import play.api.libs.json.{Json, Writes}

object Image extends DtosDefinition {

  case class ImageResponse(url: String, height: Option[Int], width: Option[Int])
  def imageToImageResponse(image: ModelImage): ImageResponse = {
    ImageResponse(image.url, image.height, image.width)
  }
  implicit val imageResponseWrites: Writes[ImageResponse] = Json.writes[ImageResponse]
}
