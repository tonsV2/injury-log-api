package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.service.impl.Credentials
import dk.fitfit.injurylog.service.impl.LoginResponse
import dk.fitfit.injurylog.service.impl.SecuredControllerTest
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import java.net.URL
import javax.inject.Inject
import kotlin.test.assertEquals

@Client("/")
interface UserClient {
    @Get("/principal")
    fun getPrincipal(@Header authorization: String): DefaultAuthentication
}

@MicronautTest
internal class UserControllerTest : SecuredControllerTest() {
    @Inject
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Inject
    lateinit var userClient: UserClient

    @Test
    fun getPrincipal_usingDeclarativeClient() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)

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
