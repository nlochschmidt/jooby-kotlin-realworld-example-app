package io.realworld.users.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordTest {

  @Test
  fun `encrypting two times yields different results`() {
    val clearTextPassword = Password("supersecret".toCharArray())
    val firstEncrypted = EncryptedPassword.encrypt(clearTextPassword)
    val secondEncrypted = EncryptedPassword.encrypt(clearTextPassword)

    assertNotEquals(firstEncrypted, secondEncrypted)
  }

  @Test
  fun `encrypted password can be matched`() {
    val clearTextPasswordOne = Password("password1".toCharArray())
    val clearTextPasswordTwo = Password("password2".toCharArray())
    val encryptedPasswordOne = EncryptedPassword.encrypt(clearTextPasswordOne)

    assertTrue(encryptedPasswordOne.matches(clearTextPasswordOne))
    assertFalse(encryptedPasswordOne.matches(clearTextPasswordTwo))
  }
}
