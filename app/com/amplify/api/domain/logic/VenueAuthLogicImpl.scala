package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.{Identifier, Name}
import com.amplify.api.domain.models.{User, Venue}
import com.amplify.api.exceptions.{AppExceptionCode, BadRequestException}
import com.amplify.api.services.{AuthenticationService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signUp(
      name: Name[Venue],
      authProviderType: AuthProviderType,
      authToken: String): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authProviderType, authToken)
      creationResult ← venueService.create(name, userData, authProviderType)
    } yield creationResult
  }

  override def login(authProviderType: AuthProviderType, authToken: String): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authProviderType, authToken)
      _ ← venueService.get(userData, authProviderType)
    }
    yield ()
  }
}

case class VenueNotFound(authProviderType: AuthProviderType, identifier: Identifier[User])
  extends BadRequestException(AppExceptionCode.VenueNotFound,
    s"Venue with identifier $identifier not found")
