package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos.Page
import com.amplify.api.services.external.spotify.JsonConverters._
import com.amplify.api.utils.WsClient
import play.api.libs.json.{Format, JsValue, Json, Reads}
import play.api.libs.ws.WSResponse
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

  def spotifyPut(
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: String): Future[WSResponse] = {
    super.apiPut(path, body, query, withAuthHeader(headers, token))
  }

  def spotifyDelete(
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: String): Future[WSResponse] = {
    super.apiDelete(path, query, withAuthHeader(headers, token))
  }

  def paginatedFetch[T: Format](
      path: String,
      query: Map[String, String],
      acc: Seq[T],
      offset: Int)(
      implicit token: String): Future[Seq[T]] = {
    fetchPage[T](path, query, offset).flatMap { page ⇒
      val items = acc ++ page.items
      page.next match {
        case Some(_) ⇒ paginatedFetch(path, query, items, offset + page.total)
        case _ ⇒ Future.successful(items)
      }
    }
  }

  def fetchPage[T: Format](
      path: String,
      query: Map[String, String],
      offset: Int)(
      implicit token: String): Future[Page[T]] = {
    spotifyGet[Page[T]](path, query.updated("offset", offset.toString))
  }

  private def withAuthHeader[T](headers: Map[String, String], token: String) = {
    headers.updated(AUTHORIZATION, s"Bearer $token")
  }
}
