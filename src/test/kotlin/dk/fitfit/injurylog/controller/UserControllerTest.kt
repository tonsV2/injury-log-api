package dk.fitfit.injurylog.controller

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.controller.client.UserClient
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@MicronautTest
internal class UserControllerTest(private val authenticationConfiguration: AuthenticationConfiguration, private val userClient: UserClient) : SecuredControllerTest() {
    @Test
    fun `Get all users`() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.adminUserEmail, authenticationConfiguration.adminUserPassword)

        // When
        val response = userClient.getUsers(authorization)

        // Then
        val users = response.body.get()
        val adminUser = users.first { it.email == authenticationConfiguration.adminUserEmail }
        val testUser = users.first { it.email == authenticationConfiguration.testUserEmail }

        assertEquals(2, users.count())
        assertEquals(authenticationConfiguration.adminUserEmail, adminUser.email)
        assertEquals(authenticationConfiguration.testUserEmail, testUser.email)
    }

    @Test
    fun `Get principal`() {
        // Given
        val authorization = getAuthorization(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)

        // When
        val response = userClient.getPrincipal(authorization)

        // Then
        val user = response.body.get()
        assertEquals(authenticationConfiguration.testUserEmail, user.name)
    }
}
