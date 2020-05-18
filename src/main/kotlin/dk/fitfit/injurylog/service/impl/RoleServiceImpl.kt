package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Role
import dk.fitfit.injurylog.repository.RoleRepository
import dk.fitfit.injurylog.service.RoleService
import javax.inject.Singleton

@Singleton
class RoleServiceImpl(private val roleRepository: RoleRepository) : RoleService {
    override fun save(role: Role): Role = roleRepository.save(role)

    override fun findAll(): Iterable<Role> = roleRepository.findAll()

    override fun get(roleName: String): Role = roleRepository.findByName(roleName) ?: throw RoleNotFoundException(roleName)
}

class RoleNotFoundException(roleName: String) : RuntimeException("Role not found: $roleName")
