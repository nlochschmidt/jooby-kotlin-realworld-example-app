package io.realworld

import org.jooby.*

class App : Kooby({

    get {
        val name = param("name").value("Jooby")
        "Hello $name!"
    }

})


fun main(args: Array<String>) {
    run(::App, *args)
}
