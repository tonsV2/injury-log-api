package dk.fitfit.injurylog.configuration

import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class AuthenticationConfiguration(@Value("\${google.server.client.id}") val serverClientId: String,
                                  @Value("\${users.admin.email}") val adminUserEmail: String,
                                  @Value("\${users.admin.password}") val adminUserPassword: String,
                                  @Value("\${users.test.email}") val testUserEmail: String,
                                  @Value("\${users.test.password}") val testUserPassword: String)
