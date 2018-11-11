package com.amplify.api.shared.controllers.dtos

import com.amplify.api.domain.models.Image
import play.api.libs.json.{Json, Writes}

object ImageDtos extends DtosDefinition {

  case class ImageResponse(url: String, height: Option[Int], width: Option[Int])
  def imageToImageResponse(image: Image): ImageResponse = {
    ImageResponse(image.url, image.height, image.width)
  }
  implicit val imageResponseWrites: Writes[ImageResponse] = Json.writes[ImageResponse]
}
