package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.service.impl.SecuredControllerTest
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
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
    fun `Get principal`() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)

        // When
        val principal = userClient.getPrincipal(authorization)

        // Then
        assertEquals(authenticationConfiguration.testUserEmail, principal.name)
    }
}
