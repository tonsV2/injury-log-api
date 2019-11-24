package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.UserService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.security.Principal

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class UserController(private val userService: UserService) {
    @Get("/users")
    fun getUsers(): Iterable<User> = userService.findAll()
}
