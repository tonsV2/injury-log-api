package dk.fitfit.injurylog

import dk.fitfit.injurylog.configuration.AdminUserConfiguration
import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.service.RoleService
import dk.fitfit.injurylog.service.UserService
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.runtime.Micronaut
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import javax.inject.Singleton

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
open class UserLoader(private val adminUserConfiguration: AdminUserConfiguration, private val userService: UserService, private val roleService: RoleService) : ApplicationEventListener<ServiceStartedEvent> {
    @Async
    override fun onApplicationEvent(event: ServiceStartedEvent) {
        log.info("Creating admin role")
        val role = roleService.save(Role("ROLE_ADMIN"))
        log.info("Role: $role")
        log.info("All roles:")
        roleService.findAll().forEach { log.info(it.name) }

        log.info("Creating admin user")
        val user = User(adminUserConfiguration.adminUserEmail)
        user.roles.add(role)
        val saved = userService.save(user)
        log.info("Admin user: $saved")
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserLoader::class.java)
    }
}
