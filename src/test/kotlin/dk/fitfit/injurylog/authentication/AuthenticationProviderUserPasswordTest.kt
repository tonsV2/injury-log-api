package dk.fitfit.injurylog.authentication

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.UserService
import dk.fitfit.injurylog.service.impl.UserNotFoundException
import io.micronaut.security.authentication.*
import io.micronaut.test.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@MicronautTest
internal class AuthenticationProviderUserPasswordTest(private val authenticationConfiguration: AuthenticationConfiguration) {
    private val userService = mockk<UserService>()
    private val googleTokenVerifier = mockk<GoogleTokenVerifier>()
    private val authenticationProvider = AuthenticationProviderUserPassword(authenticationConfiguration, userService, googleTokenVerifier)

    @Test
    fun `Fail authentication`() {
        // Given
        val username = "some username which does not exists"
        val password = "some password which does not exists"
        val authenticationRequest = UsernamePasswordCredentials(username, password)

        // When
        val flowable = authenticationProvider.authenticate(authenticationRequest) as Flowable<AuthenticationResponse>
        val authenticationResponse = flowable.blockingSingle() as AuthenticationFailed

        // Then
        assertEquals(AuthenticationFailureReason.UNKNOWN, authenticationResponse.reason)
    }

    @Test
    fun `Authenticate test user`() {
        // Given
        val authenticationRequest = UsernamePasswordCredentials(authenticationConfiguration.testUserEmail, authenticationConfiguration.testUserPassword)

        // When
        val flowable = authenticationProvider.authenticate(authenticationRequest) as Flowable<AuthenticationResponse>
        val authenticationResponse = flowable.blockingSingle() as UserDetails

        // Then
        assertEquals(authenticationConfiguration.testUserEmail, authenticationResponse.username)
        assertEquals(0, authenticationResponse.roles.size)
    }

    @Test
    fun `Authenticate admin user`() {
        // Given
        val user = User(authenticationConfiguration.adminUserEmail)
        user.roles.add(Role(Role.ADMIN))
        every { userService.getByEmail(authenticationConfiguration.adminUserEmail) } returns user

        val authenticationRequest = UsernamePasswordCredentials(authenticationConfiguration.adminUserEmail, authenticationConfiguration.adminUserPassword)

        // When
        val flowable = authenticationProvider.authenticate(authenticationRequest) as Flowable<AuthenticationResponse>
        val authenticationResponse = flowable.blockingSingle() as UserDetails

        // Then
        assertEquals(authenticationConfiguration.adminUserEmail, authenticationResponse.username)
        assertEquals(1, authenticationResponse.roles.size)
        assertTrue(authenticationResponse.roles.contains(Role.ADMIN))
        verify(exactly = 1) { userService.getByEmail(authenticationConfiguration.adminUserEmail) }
    }

    @Test
    fun `Authenticate existing user by token`() {
        // Given
        val username = "google"
        val password = "some google token"

        val email = authenticationConfiguration.adminUserEmail

        val user = User(email)
        user.roles.add(Role(Role.ADMIN))
        every { userService.getByEmail(email) } returns user

        val payload = GoogleIdToken.Payload()
        payload.email = email
        payload.emailVerified = true
        every { googleTokenVerifier.verifyToken(password) } returns payload

        val authenticationRequest = UsernamePasswordCredentials(username, password)

        // When
        val flowable = authenticationProvider.authenticate(authenticationRequest) as Flowable<AuthenticationResponse>
        val authenticationResponse = flowable.blockingSingle() as UserDetails

        // Then
        assertEquals(email, authenticationResponse.username)
        assertEquals(1, authenticationResponse.roles.size)
        assertTrue(authenticationResponse.roles.contains(Role.ADMIN))
        verify(exactly = 1) { userService.getByEmail(email) }
    }

    @Test
    fun `Authenticate nonexisting user by token`() {
        // Given
        val username = "google"
        val password = "some google token"

        val email = authenticationConfiguration.adminUserEmail

        val user = User(email)
        user.roles.add(Role(Role.ADMIN))
        every { userService.getByEmail(email) } throws UserNotFoundException(email)

        val userSlot = slot<User>()
        every { userService.save(capture(userSlot)) } returns user

        val payload = GoogleIdToken.Payload()
        payload.email = email
        payload.emailVerified = true
        every { googleTokenVerifier.verifyToken(password) } returns payload

        val authenticationRequest = UsernamePasswordCredentials(username, password)

        // When
        val flowable = authenticationProvider.authenticate(authenticationRequest) as Flowable<AuthenticationResponse>
        val authenticationResponse = flowable.blockingSingle() as UserDetails

        // Then
        assertEquals(email, userSlot.captured.email)
        assertEquals(email, authenticationResponse.username)
        assertEquals(1, authenticationResponse.roles.size)
        assertTrue(authenticationResponse.roles.contains(Role.ADMIN))
        verify(exactly = 1) { userService.getByEmail(email) }
    }
}
