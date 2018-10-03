package io.realworld.users.model

import com.google.common.io.CharSource
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class Password(val clearText: CharArray) {
  fun clear() {
    clearText.fill('\u0000')
  }
}

data class EncryptedPassword(
  internal val secretKeyFactoryId: String,
  internal val hash: ByteArray,
  internal val salt: ByteArray,
  internal val iterations: Int) {

  fun matches(password: Password) =
    hashPassword(password.clearText, salt, iterations, secretKeyFactoryId).contentEquals(hash)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as EncryptedPassword

    if (secretKeyFactoryId != other.secretKeyFactoryId) return false
    if (!Arrays.equals(hash, other.hash)) return false
    if (iterations != other.iterations) return false
    if (!Arrays.equals(salt, other.salt)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = secretKeyFactoryId.hashCode()
    result = 31 * result + Arrays.hashCode(hash)
    result = 31 * result + iterations
    result = 31 * result + Arrays.hashCode(salt)
    return result
  }

  companion object {
    var secretKeyFactoryId = "PBKDF2WithHmacSHA512"
    var iterations = 120_000
    var keyLength = 512

    fun encrypt(password: Password): EncryptedPassword {
      val salt = genSalt()
      val passwordHash = hashPassword(password.clearText, salt, iterations, secretKeyFactoryId)
      return EncryptedPassword(secretKeyFactoryId, passwordHash, salt, iterations)
    }

    private fun genSalt(): ByteArray = ByteArray(32).also(Random()::nextBytes)
    fun hashPassword(password: CharArray, salt: ByteArray, iterations: Int, secretKeyFactoryId: String): ByteArray {
      try {
        val skf = SecretKeyFactory.getInstance(secretKeyFactoryId)
        val spec = PBEKeySpec(password, salt, iterations, keyLength)
        val key = skf.generateSecret(spec)
        return key.encoded
      } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException(e)
      } catch (e: InvalidKeySpecException) {
        throw RuntimeException(e)
      }
    }
  }
}
