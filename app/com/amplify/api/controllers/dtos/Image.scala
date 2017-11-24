package com.amplify.api.controllers.dtos

import com.amplify.api.domain.models.{Image ⇒ ModelImage}
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.{Json, Writes}

object Image {

  case class ImageResponse(url: String, height: Option[Int], width: Option[Int])
  def imageToImageResponse(image: ModelImage): ImageResponse = {
    ImageResponse(image.url, image.height, image.width)
  }
  implicit val imageResponseWrites: Writes[ImageResponse] = {
    JsonNaming.snakecase(Json.writes[ImageResponse])
  }
}
