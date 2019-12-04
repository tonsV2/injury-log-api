package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.Role

interface RoleService {
    fun save(role: Role): Role
    fun findAll(): Iterable<Role>
    fun get(roleName: String): Role
}
