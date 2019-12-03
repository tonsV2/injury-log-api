package dk.fitfit.injurylog

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.RoleService
import dk.fitfit.injurylog.service.UserService
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.runtime.Micronaut
import io.micronaut.scheduling.annotation.Async
import mu.KotlinLogging
import javax.inject.Singleton

private val logger = KotlinLogging.logger {}

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("dk.fitfit.injurylog")
                .mainClass(Application.javaClass)
                .start()
    }
}

@Singleton
open class UserLoader(private val authenticationConfiguration: AuthenticationConfiguration,
                      private val userService: UserService,
                      private val roleService: RoleService) : ApplicationEventListener<ServiceStartedEvent> {
    @Async
    override fun onApplicationEvent(event: ServiceStartedEvent) {
        val role = roleService.save(Role(Role.ADMIN))
        val roles = roleService.findAll()

        val user = User(authenticationConfiguration.adminEmail)
        user.roles.add(role)
        val saved = userService.save(user)

        logger.info {
            """
                Creating admin role
                Role: ${role.name}
                All roles: ${roles.joinToString { it.name }}
                Creating admin user
                Admin user: ${saved.email}
                Admin password: ${authenticationConfiguration.adminPassword}
            """.trimIndent()
        }
    }
}
