package com.amplify.api.domain.coin

import com.amplify.api.domain.coin.CoinAuthenticatedBuilder.COIN_PARAM
import com.amplify.api.domain.models.Coin
import com.amplify.api.domain.models.primitives.Code
import com.amplify.api.shared.controllers.dtos.ClientErrorResponse
import com.amplify.api.shared.exceptions.AppExceptionCode.AuthenticationFailed
import javax.inject.Inject
import play.api.http.Status.FORBIDDEN
import play.api.mvc.Security.{AuthenticatedBuilder, AuthenticatedRequest}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

private[coin] class CoinAuthenticatedBuilder(
    parser: BodyParser[AnyContent])(
    implicit ec: ExecutionContext)
  extends AuthenticatedBuilder[Code](
    { request: RequestHeader ⇒ request.headers.get(COIN_PARAM).map(Code(_)) },
    parser) {

  @Inject()
  def this(parser: BodyParsers.Default)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent])
  }
}

object CoinAuthenticatedBuilder {

  val COIN_PARAM = "Coin"
}

private[coin] class CoinActionBuilder @Inject()(
    coinService: CoinService,
    builder: CoinAuthenticatedBuilder,
    override val parser: BodyParsers.Default)(
    override implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CoinRequest, AnyContent] {

  private val authFailure: Future[Result] =
    Future.successful(ClientErrorResponse(AuthenticationFailed, "Authentication failed", FORBIDDEN))

  override def invokeBlock[A](
      request: Request[A],
      block: CoinRequest[A] ⇒ Future[Result]): Future[Result] = {
    builder.authenticate(
      request, authenticate(_: AuthenticatedRequest[A, Code], block))
  }

  private def authenticate[A](
      request: AuthenticatedRequest[A, Code],
      block: CoinRequest[A] ⇒ Future[Result]) = {
    coinService.login(request.user).flatMap {
      case Some(coin) ⇒ block(CoinRequest(coin, request))
      case None ⇒ authFailure
    }
  }
}

private[coin] case class CoinRequest[A](coin: Coin, request: Request[A])
  extends WrappedRequest[A](request)
