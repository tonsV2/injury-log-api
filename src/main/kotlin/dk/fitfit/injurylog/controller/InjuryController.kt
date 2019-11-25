package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.dto.InjuryRequest
import dk.fitfit.injurylog.dto.InjuryResponse
import dk.fitfit.injurylog.service.InjuryService
import dk.fitfit.injurylog.service.UserService
import dk.fitfit.injurylog.service.impl.UserNotFoundException
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.security.Principal
import javax.inject.Singleton

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class InjuryController(private val userService: UserService, private val injuryService: InjuryService) {
    @Get("/injuries")
    fun getInjuries(principal: Principal): Iterable<InjuryResponse>? = userService.getByEmail(principal.name).let {
        injuryService.findAll(it).map { entity -> entity.toInjuryResponse() }
    }

    @Post("/injuries")
    fun postInjury(injuryRequest: InjuryRequest, principal: Principal): InjuryResponse = userService.getByEmail(principal.name).let {
        val injury = injuryRequest.toInjury(it)
        injuryService.save(injury).toInjuryResponse()
    }

    private fun InjuryRequest.toInjury(user: User) = Injury(description, user, occurredAt)
    private fun Injury.toInjuryResponse() = InjuryResponse(description, occurredAt, loggedAt, id)
}

@Produces
@Singleton
@Requires(classes = [UserNotFoundException::class, ExceptionHandler::class])
class UserNotFoundExceptionHandler : ExceptionHandler<UserNotFoundException, HttpResponse<*>?> {
    override fun handle(request: HttpRequest<*>?, exception: UserNotFoundException): HttpResponse<String> = HttpResponse.notFound(exception.message)
}
