package io.realworld

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.inject.Binder
import com.typesafe.config.Config
import io.realworld.users.action.CreateToken
import io.realworld.users.action.VerifyToken
import io.realworld.users.api.UsersController
import io.realworld.users.api.jwt.CreateJSONWebToken
import io.realworld.users.api.jwt.VerifyJSONWebToken
import io.realworld.users.db.Models
import org.jooby.Env
import org.jooby.Kooby
import org.jooby.MediaType
import org.jooby.json.Jackson
import org.jooby.run
import org.jooby.flyway.Flywaydb
import org.jooby.jdbc.Jdbc
import org.jooby.requery.Requery


class App : Kooby({

    use(Jdbc())
    use(Requery.kotlin(Models.DEFAULT))

    use(Flywaydb())
    use(Jackson().doWith { mapper -> mapper.registerKotlinModule()})

    use { _: Env, _: Config, binder: Binder ->
      binder.bind(CreateToken::class.java).to(CreateJSONWebToken::class.java)
      binder.bind(VerifyToken::class.java).to(VerifyJSONWebToken::class.java)
    }

    path("api") {
        get("user", UsersController::currentUser)
        path("users") {
            post("login", UsersController::login)
            post(UsersController::register)
        }
    }.consumes(MediaType.json).produces(MediaType.json)

})


fun main(args: Array<String>) {
    run(::App, *args)
}
