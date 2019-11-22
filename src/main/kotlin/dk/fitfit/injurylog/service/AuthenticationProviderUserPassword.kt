package dk.fitfit.injurylog.service

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.*
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null && authenticationRequest.identity != null && authenticationRequest.secret != null) {
            if (authenticationRequest.identity == "sherlock" && authenticationRequest.secret == "password") {
                return Flowable.just<AuthenticationResponse>(UserDetails(authenticationRequest.identity as String, ArrayList()))
            }
        }
        return Flowable.just<AuthenticationResponse>(AuthenticationFailed())
    }
}
