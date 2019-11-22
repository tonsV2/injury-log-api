package dk.fitfit.injurylog.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.security.Principal

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class HomeController {
    @Get("/home")
    fun index(principal: Principal): Principal {
        return principal
    }

    @Get("/anonymous")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun anonymous(): String {
        return "Anonymous access"
    }
}
