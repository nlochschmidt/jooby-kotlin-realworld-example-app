package io.realworld.users.action

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.realworld.users.db.UserCredentialsRepository
import io.realworld.users.db.UserRepository
import io.realworld.users.model.Bio
import io.realworld.users.model.Email
import io.realworld.users.model.Password
import io.realworld.users.model.User
import io.realworld.users.model.UserId
import io.realworld.users.model.Username
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object RegisterTest : Spek({
  describe("User registration") {

    val exampleUser = User(UserId.generate(), Email("tester@example.com"), Username("tester"), Bio(""), null)
    val userRepository = mockk<UserRepository>(relaxed = true)
    val userCredentialsRepository = mockk<UserCredentialsRepository>(relaxed = true)
    val registerService = Register(userRepository, userCredentialsRepository, { exampleUser.userId })

    on("register a user with an unknown email address") {
      clearMocks(userRepository, userCredentialsRepository)
      every { userCredentialsRepository.findByEmail(exampleUser.email) } returns null
      val providedPassword = Password("Password".toCharArray())
      val response = registerService.invoke(exampleUser.email, providedPassword, exampleUser.username)

      it("creates the user") {
        verify {
          userRepository.save(withArg {
            it.userId shouldBe exampleUser.userId
            it.email shouldBe exampleUser.email
          })
        }
      }

      it("saves the user credentials") {
        verify {
          userCredentialsRepository.save(withArg {
            it.userId shouldBe exampleUser.userId
            it.email shouldBe exampleUser.email
            it.password.matches(Password("Password".toCharArray())).shouldBeTrue()
          })
        }
      }

      it("returns a RegisteredUser in the response") {
        response.shouldBeTypeOf<UserRegistered>()
      }

      it("clears the password array") {
        providedPassword.clearText.all { it == '\u0000' }
      }
    }

    on("register a user with a known email address") {
      clearMocks(userRepository, userCredentialsRepository)

      val examplePassword = Password("Password".toCharArray())
      every { userCredentialsRepository.findByEmail(exampleUser.email) } returns mockk()
      val response = registerService.invoke(exampleUser.email, examplePassword, exampleUser.username)

      it ("does not create a new user") {
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userCredentialsRepository.save(any()) }
      }

      it ("responds that email is already taken") {
        response shouldBe EmailAlreadyTaken
      }

      it ("clears the password array") {
        examplePassword.clearText.all { it == '\u0000' }
      }
    }
  }
})
