package dk.fitfit.injurylog.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class HelloController() {
    @Get("/hello")
    fun hello(): String {
        return "Hello World!"
    }
}
