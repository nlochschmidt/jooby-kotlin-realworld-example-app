package io.realworld.users.db

import io.realworld.users.model.Bio
import io.realworld.users.model.Email
import io.realworld.users.model.ProfileImage
import io.realworld.users.model.User
import io.realworld.users.model.UserId
import io.realworld.users.model.Username
import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.Persistable
import io.requery.Table
import io.requery.sql.KotlinEntityDataStore
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(val store: KotlinEntityDataStore<UserRecord>) {

  fun findById(userId: UserId): User? = store.findByKey(UserRecord::class, userId.id)?.toUser()

  fun save(user: User): User {
    return store.insert(user.toUserRecord()).toUser()
  }

  companion object {
    @Entity
    @Table(name = "users")
    data class UserRecord (

      @get:Key
      var id: UUID,

      @get:Column(unique = true)
      val email: String,

      val username: String,
      val bio: String,
      val image: String?) : Persistable

    fun UserRecord.toUser() = User(UserId(id), Email(email), Username(username), Bio(bio), image?.let(::ProfileImage))

    fun User.toUserRecord() = UserRecord(userId.id, email.email, username.username, bio.bio, profileImage?.imageUrl)
  }
}
