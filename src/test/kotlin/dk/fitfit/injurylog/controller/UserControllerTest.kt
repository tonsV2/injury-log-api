package dk.fitfit.injurylog.controller

import com.fasterxml.jackson.annotation.JsonProperty
import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import java.net.URL
import javax.inject.Inject
import kotlin.test.assertEquals

class Credentials(val username: String, val password: String)

class LoginResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("username") val username: String)

@Client("/")
interface UserClient {
    @Post("/login")
    fun login(@Body credentials: Credentials): LoginResponse

    @Get("/principal")
    fun getPrincipal(@Header authorization: String): DefaultAuthentication
}

@MicronautTest
internal class UserControllerTest {
    @Inject
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Inject
    lateinit var userClient: UserClient

    @Test
    fun getPrincipal_usingDeclarativeClient() {
        // Given
        val credentials = Credentials(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)
        val loginResponse = userClient.login(credentials)
        val authorization = "Bearer ${loginResponse.accessToken}"

        // When
        val principal = userClient.getPrincipal(authorization)

        // Then
        assertEquals(authenticationConfiguration.testUserEmail, principal.name)
    }

    @Inject
    lateinit var server: EmbeddedServer

    @Test
    fun getPrincipal_usingManualClient() {
        // Given
        val serverUrl = URL("${server.scheme}://${server.host}:${server.port}")
        val client = HttpClient.create(serverUrl)
        val credentials = Credentials(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)
        val loginResponse = client.toBlocking().retrieve(POST("/login", credentials), LoginResponse::class.java)
        val accessToken = loginResponse.accessToken

        // When
        val principal = client.toBlocking().retrieve(GET<DefaultAuthentication>("/principal").bearerAuth(accessToken), DefaultAuthentication::class.java)

        // Then
        assertEquals(authenticationConfiguration.testUserEmail, principal.name)
    }
}
