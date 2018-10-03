package io.realworld.users.api.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.realworld.users.action.CreateToken
import io.realworld.users.action.VerifyToken
import io.realworld.users.model.User
import io.realworld.users.model.UserId
import java.util.UUID
import javax.inject.Inject

class JWTConfig {
  val issuer: String = "jooby-kotlin-realworld-example-app"
  val secret: String = "supersecretkey"
  val algorithm by lazy { Algorithm.HMAC256(secret) }
}

class CreateJSONWebToken @Inject constructor(
  private val jwtConfig: JWTConfig
) : CreateToken {

  override fun invoke(user: User): String {
    return JWT.create()
      .withIssuer(jwtConfig.issuer)
      .withSubject(user.userId.id.toString())
      .sign(jwtConfig.algorithm)
  }
}

class VerifyJSONWebToken @Inject constructor(
  private val jwtConfig: JWTConfig
) : VerifyToken {
  override fun invoke(token: String): UserId {
    return UserId(UUID.fromString(JWT.require(jwtConfig.algorithm).build().verify(token).subject))
  }
}
