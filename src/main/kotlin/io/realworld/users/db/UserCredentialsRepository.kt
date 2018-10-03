package io.realworld.users.db

import io.realworld.users.model.Email
import io.realworld.users.model.EncryptedPassword
import io.realworld.users.model.UserCredentials
import io.realworld.users.model.UserId
import io.requery.*
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import java.util.*
import javax.inject.Inject

class UserCredentialsRepository @Inject constructor(val store: KotlinEntityDataStore<UserCredentialsRecord>) {

  fun findByEmail(email: Email): UserCredentials? =
    store
      .select(UserCredentialsRecord::class)
      .where(UserCredentialsRecord::email eq (email.email))
      .get()
      .firstOrNull()
      ?.toUserCredentials()

  fun save(credentials: UserCredentials) = store.insert(credentials.toRecord())

  companion object {
    @Entity
    @Table(name = "user_credentials")
    data class UserCredentialsRecord (
      @get:Key
      val email: String,
      @get:Column(unique = true)
      val user_id: UUID,
      val password_type: String,
      val password_hash: ByteArray,
      val password_salt: ByteArray,
      val password_iterations: Int) : Persistable {

      @Transient
      fun toUserCredentials() = UserCredentials(
        UserId(user_id),
        Email(email),
        EncryptedPassword(
          password_type,
          password_hash,
          password_salt,
          password_iterations
        ))
    }
  }

  private fun UserCredentials.toRecord() = UserCredentialsRecord(
    email.email,
    userId.id,
    password.secretKeyFactoryId,
    password.hash,
    password.salt,
    password.iterations
  )
}

