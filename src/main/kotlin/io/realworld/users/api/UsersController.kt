package io.realworld.users.api

import com.google.common.net.HttpHeaders.*
import io.realworld.users.model.Email
import io.realworld.users.action.EmailAlreadyTaken
import io.realworld.users.action.Login
import io.realworld.users.action.Register
import io.realworld.users.model.Password
import io.realworld.users.action.UserRegistered
import io.realworld.users.model.Username
import org.jooby.Err
import org.jooby.Request
import org.jooby.Result
import org.jooby.Results
import org.jooby.Status.BAD_REQUEST
import org.jooby.Status.UNAUTHORIZED
import org.jooby.body
import org.jooby.require

object UsersController {
  fun login(req: Request): Result {
    val (email, password) = req.body<LoginRequestDTO>().user
    val serviceResponse = req.require(Login::class)(Email(email), Password(password)) ?: throw Err(UNAUTHORIZED)
    return Results.json(LoginResponseDTO(serviceResponse.user.toUserDTO()))
  }

  fun register(req: Request): Result {
    val (email, password, username) = req.body<RegisterRequestDTO>().user
    val serviceResponse = req.require(Register::class)(Email(email), Password(password), Username(username))
    return when (serviceResponse) {
      is UserRegistered -> {
        val userDTO = req.require(Login::class)(Email(email), Password(password))!!.user.toUserDTO()
        Results.json(LoginResponseDTO(userDTO))
      }
      EmailAlreadyTaken -> Results.json(mapOf("error" to "email taken")).status(BAD_REQUEST)
    }
  }

  fun currentUser(req: Request): Result {
    val token = req.header(AUTHORIZATION).value().removePrefix("Token ")
    val serviceResponse = req.require(Login::class)(token) ?: throw Err(UNAUTHORIZED)
    return Results.json(LoginResponseDTO(serviceResponse.user.toUserDTO()))
  }


  data class LoginRequestDTO(val user: LoginUserDTO)
  data class LoginUserDTO(val email: String, val password: CharArray)

  data class RegisterRequestDTO(val user: RegisterUserDTO)
  data class RegisterUserDTO(val email: String, val password: CharArray, val username: String)

  data class LoginResponseDTO(val user: UserDTO)
  data class UserDTO(val email: String, val username: String, val bio: String, val image: String?, val token: String)
  private fun Login.Companion.LoggedInUser.toUserDTO() = UserDTO(email.email, username.username, bio.bio, image?.imageUrl, token)
}

