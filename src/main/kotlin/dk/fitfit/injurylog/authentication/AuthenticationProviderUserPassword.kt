package dk.fitfit.injurylog.authentication

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.UserService
import dk.fitfit.injurylog.service.impl.UserNotFoundException
import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(private val authenticationConfiguration: AuthenticationConfiguration, private val userService: UserService) : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null) {
            if (authenticationRequest.identity == "_" && authenticationRequest.secret != null) {
                verifyToken(authenticationRequest.secret as String)?.let {
                    if (it.email != null && it.emailVerified) {
                        // TODO: If payload.emailVerified == false create AuthenticationFailureReason and pass to AuthenticationFailed
                        val user = createUserIfNotFound(it.email)
                        val roles = user.roles.map { role -> role.name }
                        return Flowable.just(UserDetails(it.email, roles))
                    }
                }
            }

            if (authenticationRequest.identity == authenticationConfiguration.adminUserEmail && authenticationRequest.secret == authenticationConfiguration.adminUserPassword) {
                val user = userService.getByEmail(authenticationConfiguration.adminUserEmail)
                val roles = user.roles.map { role -> role.name }
                return Flowable.just(UserDetails(authenticationConfiguration.adminUserEmail, roles))
            }

            if (authenticationRequest.identity == authenticationConfiguration.testUserEmail && authenticationRequest.secret == authenticationConfiguration.testUserPassword) {
                return Flowable.just(UserDetails(authenticationConfiguration.testUserEmail, listOf()))
            }
        }

        return Flowable.just(AuthenticationFailed())
    }

    private fun createUserIfNotFound(email: String): User {
        return try {
            userService.getByEmail(email)
        } catch (e: UserNotFoundException) {
            userService.save(User(email))
        }
    }

    private fun verifyToken(secret: String): GoogleIdToken.Payload? {
        val transport = NetHttpTransport()
        val jsonFactory = JacksonFactory()
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(listOf(authenticationConfiguration.webClientId, authenticationConfiguration.androidClientId))
                .build()
        val token = verifier.verify(secret)
        return token?.payload
    }
}
