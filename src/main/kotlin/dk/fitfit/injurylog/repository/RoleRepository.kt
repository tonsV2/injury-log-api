package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Role
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface RoleRepository : CrudRepository<Role, Long> {
    fun findByName(name: String): Role?
}
