package io.realworld.users.model

data class UserCredentials(val userId: UserId, val email: Email, val password: EncryptedPassword)
