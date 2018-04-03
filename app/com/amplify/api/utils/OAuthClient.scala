package com.amplify.api.utils

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.exceptions.{AppExceptionCode, InternalException}
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.{WSResponse, WSClient ⇒ PlayClient}
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, Future}

trait OAuthClient {

  implicit val ec: ExecutionContext
  val ws: PlayClient
  val baseUrl: String

  private val MAX_WAIT_RESPONSE = 20.seconds

  private lazy val logger = Logger(classOf[OAuthClient])

  def apiGet[T](
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T],
      token: AuthToken): Future[T] = {
    apiCallForResponse[T]("GET", path, query, headers)
  }

  def apiPost[T](
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T],
      token: AuthToken): Future[T] = {
    apiCallForResponse[T]("POST", path, query, headers, Json.stringify(body))
  }

  def apiPut(
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: AuthToken): Future[WSResponse] = {
    apiCall("PUT", path, query, headers, Json.stringify(body))
  }

  def apiDelete(path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: AuthToken): Future[WSResponse] = {
    apiCall("DELETE", path, query, headers)
  }

  def apiCall(
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: String = "")(
      implicit token: AuthToken): Future[WSResponse] = {
    val authorizedHeaders = headers.updated(AUTHORIZATION, s"Bearer ${token.token}")

    val request =
      ws.url(s"$baseUrl$path")
        .withRequestTimeout(MAX_WAIT_RESPONSE)
        .addHttpHeaders(authorizedHeaders.toSeq: _*)
        .addQueryStringParameters(query.toSeq: _*)

    val requestWithBody = if (body.isEmpty) request else request.withBody(body)

    logger.debug(s"Sending $method to $baseUrl$path " +
      s"with headers: $headers query: $query and body $body")
    requestWithBody.execute(method).flatMap(customHandleResponse)
  }

  protected def apiCallForResponse[T](
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: String = "")(
      implicit reads: Reads[T],
      token: AuthToken): Future[T] = {
    apiCall(method, path, query, headers, body).flatMap(r ⇒ validate[T](r.json))
  }

  protected def customHandleResponse(response: WSResponse): Future[WSResponse] = {
    Future.successful(response)
  }

  protected def validate[T](json: JsValue)(implicit reads: Reads[T]): Future[T] = {
    json.validate[T].asEither match {
      case Left(errors) =>
        val errorsString = errors.map(e ⇒ s"${e._1}: ${e._2.map(_.message)}").mkString(", ")
        Future.failed(InvalidJsonException(s"$errorsString json: ${json.toString()}"))
      case Right(t) =>
        Future.successful(t)
    }
  }
}

case class InvalidJsonException(override val message: String)
  extends InternalException(AppExceptionCode.UnexpectedExternalServiceResponse, message)
