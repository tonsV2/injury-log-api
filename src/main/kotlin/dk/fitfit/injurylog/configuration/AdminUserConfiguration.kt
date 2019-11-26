package dk.fitfit.injurylog.configuration

import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class AdminUserConfiguration(@Value("\${admin.user.email}") val adminUserEmail: String)
