package io.realworld.users.model

import java.util.*

data class UserId(val id: UUID) {
  companion object {
      fun generate(): UserId = UserId(UUID.randomUUID())
  }
}

data class Email(val email: String)
data class Bio(val bio: String)
data class Username(val username: String)
data class ProfileImage(val imageUrl: String)

class User(
  val userId: UserId,
  val email: Email,
  val username: Username,
  val bio: Bio,
  val profileImage: ProfileImage?
)


