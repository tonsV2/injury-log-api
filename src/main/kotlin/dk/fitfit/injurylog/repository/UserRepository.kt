package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    @Query("from User u where u.email = :email")
    fun findByEmail(email: String): User?
}
