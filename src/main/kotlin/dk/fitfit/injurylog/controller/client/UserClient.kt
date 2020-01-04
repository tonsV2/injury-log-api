package dk.fitfit.injurylog.controller.client

import dk.fitfit.injurylog.domain.User
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import io.micronaut.security.authentication.DefaultAuthentication

@Client("/")
interface UserClient {
    @Get("/principal")
    fun getPrincipal(@Header authorization: String): HttpResponse<DefaultAuthentication>

    @Get("/users")
    fun getUsers(@Header authorization: String): HttpResponse<Iterable<User>>
}
