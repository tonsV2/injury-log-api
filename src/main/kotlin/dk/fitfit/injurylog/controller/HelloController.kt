package dk.fitfit.injurylog.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
class HelloController() {
    @Get("/hello")
    fun hello(): String {
        return "Hello World!"
    }
}
