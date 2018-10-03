package io.realworld.users.action

import com.google.inject.ImplementedBy
import io.realworld.users.db.UserCredentialsRepository
import io.realworld.users.db.UserRepository
import io.realworld.users.model.Bio
import io.realworld.users.model.Email
import io.realworld.users.model.Password
import io.realworld.users.model.ProfileImage
import io.realworld.users.model.User
import io.realworld.users.model.UserId
import io.realworld.users.model.Username
import javax.inject.Inject

class Login @Inject constructor(
  private val userRepository: UserRepository,
  private val userCredentialsRepository: UserCredentialsRepository,
  private val createToken: CreateToken,
  private val verifyToken: VerifyToken) {

  operator fun invoke(email: Email, password: Password): UserResponse? {
    return userCredentialsRepository.findByEmail(email)
      ?.takeIf { it.password.matches(password) }
      ?.let { userCredentials ->
        invoke(userCredentials.userId)
      }.also {
        password.clear()
      }
  }

  operator fun invoke(userId: UserId): UserResponse? {
    return userRepository
      .findById(userId)
      ?.run { LoggedInUser(email, createToken(this), username, bio, profileImage) }
      ?.let {
        UserResponse(it)
      }
  }

  operator fun invoke(token: String): UserResponse? {
    return invoke(verifyToken(token))
  }

  companion object {
    data class UserResponse(val user: LoggedInUser)
    data class LoggedInUser(
      val email: Email,
      val token: String,
      val username: Username,
      val bio: Bio,
      val image: ProfileImage?)
  }
}

interface CreateToken {
  operator fun invoke(user: User): String
}

interface VerifyToken {
  operator fun invoke(token: String): UserId
}
