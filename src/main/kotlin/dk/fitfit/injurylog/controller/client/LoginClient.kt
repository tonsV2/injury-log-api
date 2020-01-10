package dk.fitfit.injurylog.controller.client

import dk.fitfit.injurylog.dto.Credentials
import dk.fitfit.injurylog.dto.LoginResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("/")
interface LoginClient {
    @Post("/login")
    fun login(@Body credentials: Credentials): LoginResponse
}
