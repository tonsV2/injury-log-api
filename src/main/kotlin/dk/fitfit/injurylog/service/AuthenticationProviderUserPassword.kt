package dk.fitfit.injurylog.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import dk.fitfit.injurylog.configuration.AuthenticationProviderConfiguration
import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.*
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(private val authenticationProviderConfiguration: AuthenticationProviderConfiguration) : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null) {
            if (authenticationRequest.identity == "_" && authenticationRequest.secret != null) {
                val payload = verifyToken(authenticationRequest.secret as String)
                if (payload != null && payload.email != null && payload.emailVerified) {
                    // TODO: Lookup or create user
                    return Flowable.just<AuthenticationResponse>(UserDetails(payload.email, ArrayList()))
                }
            }
        }
        return Flowable.just<AuthenticationResponse>(AuthenticationFailed())
    }

    private fun verifyToken(secret: String): GoogleIdToken.Payload? {
        val transport = NetHttpTransport()
        val jsonFactory = JacksonFactory()
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(listOf(authenticationProviderConfiguration.serverClientId))
                .build()
        val token = verifier.verify(secret)
        return token?.payload
    }
}
