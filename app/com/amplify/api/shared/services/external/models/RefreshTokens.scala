package com.amplify.api.shared.services.external.models

import com.amplify.api.domain.models.primitives.{Access, Refresh, Token}

case class RefreshTokens(refreshToken: Token[Refresh], accessToken: Token[Access])
