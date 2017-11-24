package com.amplify.api.aggregates.queue.services.external.firebase

import com.amplify.api.aggregates.queue.services.external.NotificationStrategy
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.domain.models.{AuthProviderType, AuthToken}
import com.amplify.api.aggregates.queue.services.external.firebase.Dtos.SendNotificationResponse
import com.amplify.api.utils.OAuthClient
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.concurrent.{ExecutionContext, Future}

class FirebaseNotificationProvider @Inject()(
    val ws: WSClient,
    envConfig: EnvConfig)(
    implicit val ec: ExecutionContext) extends NotificationStrategy with OAuthClient {

  override val baseUrl = envConfig.firebaseSendEndpoint

  private val scopes = "https://www.googleapis.com/auth/firebase.messaging"

  private val privateKeyBytes = envConfig.firebasePrivateKey.getBytes(StandardCharsets.UTF_8)
  private val googleCredential =
    GoogleCredential
      .fromStream(new ByteArrayInputStream(privateKeyBytes))
      .createScoped(List(scopes).asJava)

  implicit private def accessToken: AuthToken = {
    googleCredential.refreshToken()
    AuthToken(AuthProviderType.Firebase, googleCredential.getAccessToken)
  }

  override def pushNotification(notification: String, destination: Identifier): Future[Unit] = {
    val body =
      Json.obj(
        "message" → Json.obj(
          "token" → destination.value,
          "notification" → Json.obj(
            "body" → notification,
            "title" → "Amplify")))

    apiPost[SendNotificationResponse](path = "", body = body).map(_ ⇒ ())
  }
}
