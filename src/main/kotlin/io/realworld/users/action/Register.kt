package io.realworld.users.action

import io.realworld.users.db.UserCredentialsRepository
import io.realworld.users.db.UserRepository
import io.realworld.users.model.*
import javax.inject.Inject

class Register(
  private val userRepository: UserRepository,
  private val userCredentialsRepository: UserCredentialsRepository,
  private val generateUserId: () -> UserId) {

  @Inject constructor(
    userRepository: UserRepository,
    userCredentialsRepository: UserCredentialsRepository
  ) : this(userRepository, userCredentialsRepository, UserId.Companion::generate)

  operator fun invoke(email: Email, password: Password, username: Username): RegistrationResponse {
    return if (userCredentialsRepository.findByEmail(email) == null) {
      val userId = generateUserId()
      val newUser = User(userId, email, username, Bio(""), profileImage = null)
      val newUserCredentials = UserCredentials(userId, email, EncryptedPassword.encrypt(password))
      userRepository.save(newUser)
      userCredentialsRepository.save(newUserCredentials)
      with(newUser) { UserRegistered(email, username, bio, profileImage) }
    } else {
      EmailAlreadyTaken
    }
  }
}

sealed class RegistrationResponse
data class UserRegistered(val email: Email, val username: Username, val bio: Bio, val profileImage: ProfileImage?) : RegistrationResponse()
object EmailAlreadyTaken : RegistrationResponse()
