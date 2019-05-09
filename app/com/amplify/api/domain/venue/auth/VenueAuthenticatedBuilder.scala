package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.venue.auth.VenueAuthenticatedBuilder.VENUE_UID
import com.amplify.api.shared.controllers.dtos.ClientErrorResponse
import com.amplify.api.shared.exceptions.AppExceptionCode.AuthenticationFailed
import javax.inject.Inject
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import play.mvc.Http.Status.FORBIDDEN
import scala.concurrent.{ExecutionContext, Future}
import scala.language.reflectiveCalls

private[venue] class VenueAuthenticatedBuilder(
    parser: BodyParser[AnyContent])(
    implicit ec: ExecutionContext)
  extends AuthenticatedBuilder[Uid](
    { request: RequestHeader ⇒ request.session.get(VENUE_UID).map(Uid(_)) },
    parser) {

  @Inject()
  def this(parser: BodyParsers.Default)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent])
  }
}

object VenueAuthenticatedBuilder {

  val VENUE_UID = "venue-uid"
}

private[venue] class VenueActionBuilder @Inject()(
    venueAuthService: VenueAuthService,
    builder: VenueAuthenticatedBuilder,
    override val parser: BodyParsers.Default)(
    override implicit val executionContext: ExecutionContext)
  extends ActionBuilder[VenueRequest, AnyContent] {

  private val authFailure: Future[Result] =
    Future.successful(ClientErrorResponse(AuthenticationFailed, "Authentication failed", FORBIDDEN))

  override def invokeBlock[A](
      request: Request[A],
      block: VenueRequest[A] ⇒ Future[Result]): Future[Result] = {
    builder.authenticate(
      request,
      authenticate(_: Security.AuthenticatedRequest[A, Uid], block))
  }

  private def authenticate[A](
      request: Security.AuthenticatedRequest[A, Uid],
      block: VenueRequest[A] ⇒ Future[Result]) = {
    venueAuthService.login(request.user).flatMap {
      case Some(venue) ⇒ block(VenueRequest(venue, request))
      case None ⇒ authFailure
    }
  }
}

private[venue] case class VenueRequest[A](venue: Venue, request: Request[A])
  extends WrappedRequest[A](request)
