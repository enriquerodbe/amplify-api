package com.amplify.api.utils

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.exceptions.{AppExceptionCode, InternalException}
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.{BodyWritable, WSResponse, WSClient ⇒ PlayClient}
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
    authorizedApiCallForResponse[Nothing, T]("GET", path, query, headers)
  }

  def apiPost[T](
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T],
      token: AuthToken): Future[T] = {
    authorizedApiCallForResponse[JsValue, T]("POST", path, query, headers, Some(body))
  }

  def apiPostFormData[T](
      path: String,
      body: Map[String, Seq[String]] = Map.empty,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit reads: Reads[T]): Future[T] = {
    unauthorizedApiCallForResponse[Map[String, Seq[String]], T](
      "POST", path, query, headers, Some(body))
  }

  def apiPut(
      path: String,
      body: JsValue = Json.obj(),
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: AuthToken): Future[WSResponse] = {
    authorizedCall("PUT", path, query, headers, Some(body))
  }

  def apiDelete(path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty)(
      implicit token: AuthToken): Future[WSResponse] = {
    authorizedCall("DELETE", path, query, headers)
  }

  def unauthorizedCall[T](
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: Option[T] = None)(
      implicit writeable: BodyWritable[T]): Future[WSResponse] = {
    val request =
      ws.url(s"$baseUrl$path")
        .withRequestTimeout(MAX_WAIT_RESPONSE)
        .addHttpHeaders(headers.toSeq: _*)
        .addQueryStringParameters(query.toSeq: _*)

    val requestWithBody = body.map(request.withBody(_)(writeable)).getOrElse(request)

    logger.warn(s"Sending $method to $baseUrl$path " +
      s"with headers: $headers query: $query and body $body")
    requestWithBody.execute(method).flatMap(customHandleResponse)
  }

  def authorizedCall[T](
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: Option[T] = None)(
      implicit token: AuthToken,
      writeable: BodyWritable[T]): Future[WSResponse] = {
    val authorizedHeaders = headers.updated(AUTHORIZATION, s"Bearer ${token.token}")
    unauthorizedCall(method, path, query, authorizedHeaders, body)
  }

  protected def unauthorizedApiCallForResponse[B, T](
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: Option[B] = None)(
      implicit reads: Reads[T],
      writeable: BodyWritable[B]): Future[T] = {
    unauthorizedCall(method, path, query, headers, body).flatMap(r ⇒ validate[T](r.json))
  }

  protected def authorizedApiCallForResponse[B, T](
      method: String,
      path: String,
      query: Map[String, String] = Map.empty,
      headers: Map[String, String] = Map.empty,
      body: Option[B] = None)(
      implicit reads: Reads[T],
      writeable: BodyWritable[B],
      token: AuthToken): Future[T] = {
    authorizedCall(method, path, query, headers, body).flatMap(r ⇒ validate[T](r.json))
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
