package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
}
