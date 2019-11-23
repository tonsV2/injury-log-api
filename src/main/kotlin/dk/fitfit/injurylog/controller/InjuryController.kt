package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.dto.InjuryRequest
import dk.fitfit.injurylog.service.InjuryService
import dk.fitfit.injurylog.service.UserService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.security.Principal
import java.time.LocalDateTime

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class InjuryController(private val userService: UserService, private val injuryService: InjuryService) {
    @Get("/injuries")
    fun getInjuries(principal: Principal): Iterable<Injury>? {
        val user = userService.findByEmail(principal.name)
        return if (user != null) {
            injuryService.findAll(user)
        } else {
            null
        }
    }

    @Post("/injuries")
    fun postInjury(injuryRequest: InjuryRequest, principal: Principal): Injury? {
        val user = userService.findByEmail(principal.name)
        return if (user != null) {
            val occurrence: LocalDateTime = injuryRequest.occurrence ?: LocalDateTime.now()
            val injury = Injury(injuryRequest.description, user, occurrence)
            injuryService.save(injury)
        } else {
            null
        }
    }
}
