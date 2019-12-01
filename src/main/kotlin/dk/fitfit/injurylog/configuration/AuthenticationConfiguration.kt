package dk.fitfit.injurylog.configuration

import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class AuthenticationConfiguration(@Value("\${google.server.client.id}") val serverClientId: String,
                                  @Value("\${admin.user.email}") val adminEmail: String)
