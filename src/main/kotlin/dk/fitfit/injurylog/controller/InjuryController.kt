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
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.multipart.CompletedFileUpload
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

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Post("/injuries/{id}/images")
    fun postImage(id: Long, file: CompletedFileUpload, principal: Principal): HttpResponse<*>? {
        if (file.filename == null || file.filename == "") {
            return HttpResponse.noContent<Any>()
        }
        val imageReference = userService.getByEmail(principal.name).let {
            injuryService.addImage(it, id, file)
        }
        return HttpResponse.ok(imageReference)
    }

    @Delete("/injuries/{injuryId}/images/{imageId}")
    fun deleteImage(injuryId: Long, imageId: Long, principal: Principal): HttpResponse<Any>? {
        userService.getByEmail(principal.name).let {
            injuryService.deleteImage(it, injuryId, imageId)
            return HttpResponse.ok()
        }
    }

// TODO: https://stackoverflow.com/questions/53592685/how-to-download-stream-large-generated-file-in-micronaut
// TODO: https://stackoverflow.com/questions/40262512/how-to-get-outputstream-from-an-s3object
    @Get("/injuries/{injuryId}/images/{imageId}")
    fun getImage(injuryId: Long, imageId: Long, principal: Principal) {
        val injury = userService.getByEmail(principal.name).let {
//            injuryService.getImage(it, injuryId, imageId)
        }
    }

    private fun InjuryRequest.toInjury(user: User) = Injury(description, user, occurredAt)
    private fun Injury.toInjuryResponse() = InjuryResponse(description, occurredAt, loggedAt, imageReferences.map { it.id }, id)
//    private fun Injury.toInjuryResponse(imageUris: List<URI>) = InjuryResponse(description, occurredAt, loggedAt, imageUris, id)
}

@Produces
@Singleton
@Requires(classes = [UserNotFoundException::class, ExceptionHandler::class])
class UserNotFoundExceptionHandler : ExceptionHandler<UserNotFoundException, HttpResponse<*>?> {
    override fun handle(request: HttpRequest<*>?, exception: UserNotFoundException): HttpResponse<String> = HttpResponse.notFound(exception.message)
}
