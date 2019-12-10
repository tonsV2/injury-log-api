package dk.fitfit.injurylog.service.impl

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import javax.inject.Inject

class Credentials(val username: String, val password: String)

class LoginResponse(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("expires_in") val expiresIn: Int,
        @JsonProperty("refresh_token") val refreshToken: String,
        @JsonProperty("token_type") val tokenType: String,
        @JsonProperty("username") val username: String)

@Client("/")
interface LoginClient {
    @Post("/login")
    fun login(@Body credentials: Credentials): LoginResponse
}

internal open class SecuredControllerTest {
    @Inject
    lateinit var loginClient: LoginClient

    internal open fun getAuthorization(username: String, password: String): String {
        val credentials = Credentials(username, password)
        val loginResponse = loginClient.login(credentials)
        return "Bearer ${loginResponse.accessToken}"
    }
}
