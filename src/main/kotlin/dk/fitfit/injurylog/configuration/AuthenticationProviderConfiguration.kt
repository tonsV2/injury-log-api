package dk.fitfit.injurylog.configuration

import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class AuthenticationProviderConfiguration(@Value("\${google.server.client.id}") val serverClientId: String)
