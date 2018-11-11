package com.amplify.api.shared.services.external.spotify

import com.amplify.api.domain.models.ContentIdentifier
import com.amplify.api.domain.models.primitives.{Access, Token}
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions._
import com.amplify.api.shared.services.external.spotify.SpotifyBaseClient._
import javax.inject.Inject
import play.api.Logger
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{Format, JsValue, Reads, __}
import play.api.libs.ws.{WSRequest, WSResponse, WSClient ⇒ PlayClient}
import play.mvc.Http
import play.mvc.Http.HeaderNames
import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, Future}

class SpotifyBaseClient @Inject()(
    envConfig: EnvConfig,
    ws: PlayClient)(
    implicit ec: ExecutionContext) {

  val apiUrl = envConfig.spotifyWebApiUrl
  val accountsUrl = envConfig.spotifyAccountsUrl
  val itemsField = "items"
  val totalField = "total"
  val nextField = "next"
  val paginationOffsetHeader = "offset"
  val MAX_WAIT_RESPONSE = 20.seconds

  private def createRequest(baseUrl: String, path: String): WSRequest = {
    ws.url(s"$baseUrl$path").withRequestTimeout(MAX_WAIT_RESPONSE)
  }

  def apiRequest(path: String): WSRequest = createRequest(apiUrl, path)

  def accountsRequest(path: String): WSRequest = createRequest(accountsUrl, path)

  case class Page[T](items: Seq[T], total: Int, next: Option[String])

  implicit def pageFormat[T: Format]: Format[Page[T]] = {
    ((__ \ itemsField).format[Seq[T]] ~
    (__ \ totalField).format[Int] ~
    (__ \ nextField).formatNullable[String])(Page.apply, unlift(Page.unapply[T]))
  }

  def paginatedFetch[T: Format](
      path: String,
      accessToken: Token[Access],
      query: Map[String, String] = Map.empty,
      acc: Seq[T] = Seq.empty,
      offset: Int = 0): Future[Seq[T]] = {
    fetchPage[T](path, query, offset, accessToken).flatMap { page ⇒
      val items = acc ++ page.items
      page.next match {
        case Some(_) ⇒ paginatedFetch(path, accessToken, query, items, offset + page.total)
        case _ ⇒ Future.successful(items)
      }
    }
  }

  private def fetchPage[T: Format](
      path: String,
      query: Map[String, String],
      offset: Int,
      accessToken: Token[Access]): Future[Page[T]] = {
    apiRequest(path)
      .withBearerToken(accessToken)
      .withQueryStringParameters(query.updated(paginationOffsetHeader, offset.toString).toSeq: _*)
      .get()
      .parseJson[Page[T]]
  }
}

object SpotifyBaseClient {

  private lazy val logger = Logger(classOf[SpotifyBaseClient])

  private def validate[T](json: JsValue)(implicit reads: Reads[T]): Future[T] = {
    json.validate[T].asEither match {
      case Left(errors) =>
        val errorsString = errors.map(e ⇒ s"${e._1}: ${e._2.map(_.message)}").mkString(", ")
        Future.failed(InvalidJsonException(s"$errorsString json: ${json.toString()}"))
      case Right(t) =>
        Future.successful(t)
    }
  }

  private def customHandleResponse(response: WSResponse): Future[WSResponse] = {
    response.status match {
      case Http.Status.OK | Http.Status.NO_CONTENT ⇒
        Future.successful(response)
      case Http.Status.UNAUTHORIZED ⇒
        Future.failed(UserAuthTokenNotFound)
      case Http.Status.NOT_FOUND ⇒
        Future.failed(ExternalResourceNotFound)
      case other ⇒
        val message = s"Unexpected status $other from Spotify. " +
          s"Headers: ${response.headers}. Body: ${response.body}"
        Future.failed(UnexpectedResponse(message))
    }
  }

  implicit class RequestBuilder(wsRequest: WSRequest) {

    def logRequest(implicit ec: ExecutionContext): WSRequest = {
      logger.warn(s"Sending request to ${wsRequest.url} " +
        s"with headers ${wsRequest.headers} and body ${wsRequest.body}")
      wsRequest
    }

    def withBearerToken(token: Token[Access]): WSRequest = {
      wsRequest.withHttpHeaders(HeaderNames.AUTHORIZATION → s"Bearer ${token.value}")
    }
  }

  implicit class ResponseHandler(wsResponse: Future[WSResponse])(implicit ec: ExecutionContext) {

    def parseJson[T](implicit reads: Reads[T]): Future[T] = {
      wsResponse.flatMap(customHandleResponse).flatMap(r ⇒ validate[T](r.json))
    }

    def logResponse(): Future[WSResponse] = {
      wsResponse.map { response ⇒
        logger.warn(s"Got response with status ${response.status} and body ${response.body}")
        response
      }
    }

    def emptyResponse(): Future[Unit] = wsResponse.flatMap(customHandleResponse).map(_ ⇒ ())
  }

  implicit class NotFoundHandler[T](wsResponse: Future[T])(implicit ec: ExecutionContext) {

    def handleNotFound(contentIdentifier: ContentIdentifier): Future[T] = {
      wsResponse.recoverWith {
        case ExternalResourceNotFound ⇒
          Future.failed(RequestedResourceNotFound(contentIdentifier.toString)): Future[T]
      }
    }
  }
}

case class InvalidJsonException(override val message: String)
  extends InternalException(AppExceptionCode.UnexpectedExternalServiceResponse, message)
