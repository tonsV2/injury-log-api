package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.controller.client.UserClient
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@MicronautTest
internal class UserControllerTest(private val authenticationConfiguration: AuthenticationConfiguration, private val userClient: UserClient) : SecuredControllerTest() {
    @Test
    fun `Get all users`() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.adminUserEmail, authenticationConfiguration.adminUserPassword)

        // When
        val users = userClient.getUsers(authorization)

        // Then
        val adminUser = users.first { it.email == authenticationConfiguration.adminUserEmail }
        val testUser = users.first { it.email == authenticationConfiguration.testUserEmail }

        assertEquals(2, users.count())
        assertNotNull(adminUser)
        assertNotNull(testUser)
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
