package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.UserService
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

    @Get("/users")
    fun getUsers(@Header authorization: String): Iterable<User>
}

@MicronautTest
internal class UserControllerTest(private val userService: UserService) : SecuredControllerTest() {
    @Inject
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Inject
    lateinit var userClient: UserClient

    @Test
    fun `Get all users`() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.adminUserEmail, authenticationConfiguration.adminUserPassword)

        // When
        val users = userClient.getUsers(authorization)

        // Then
        assertEquals(2, users.count())
    }

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
