package dk.fitfit.injurylog.util

import dk.fitfit.injurylog.configuration.AuthenticationConfiguration
import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.RoleService
import dk.fitfit.injurylog.service.UserService
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.scheduling.annotation.Async
import mu.KotlinLogging
import javax.inject.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class DataLoader(
        private val authenticationConfiguration: AuthenticationConfiguration,
        private val userService: UserService,
        private val roleService: RoleService
) : ApplicationEventListener<ServiceStartedEvent> {
    @Async
    override fun onApplicationEvent(event: ServiceStartedEvent) {
        val role = roleService.save(Role(Role.ADMIN))
        val roles = roleService.findAll()

        val adminUser = User(authenticationConfiguration.adminUserEmail)
        adminUser.roles.add(role)
        val savedAdmin = userService.save(adminUser)

        val testUser = User(authenticationConfiguration.testUserEmail)
        val savedTest = userService.save(testUser)

        val users = userService.findAll()

        logger.info {
            """
                Creating admin role
                Role: ${role.name}
                All roles: ${roles.joinToString { it.name }}
                Creating admin user
                Email: ${savedAdmin.email}
                Password: ${authenticationConfiguration.adminUserPassword}
                Creating test user
                Email: ${savedTest.email}
                Password: ${authenticationConfiguration.testUserPassword}
                All users: ${users.joinToString { it.email }}
            """.trimIndent()
        }
    }
}
