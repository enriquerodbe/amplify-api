package com.amplify.api.services.external.spotify

import com.amplify.api.utils.WsClient
import play.api.libs.json.{JsValue, Json, Reads}
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.Future

trait SpotifyBaseClient extends WsClient {

  def spotifyGet[T](
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T],
      token: String): Future[T] = {
    super.apiGet(path, query, withAuthHeader(headers, token))
  }

  def spotifyPost[T](
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T],
      token: String): Future[T] = {
    super.apiPost(path, body, query, withAuthHeader(headers, token))
  }

  def spotifyDelete(
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: String): Future[JsValue] = {
    super.apiDelete(path, query, withAuthHeader(headers, token))
  }

  private def withAuthHeader[T](headers: Map[String, String], token: String) = {
    headers.updated(AUTHORIZATION, s"Bearer $token")
  }
}
