package dk.fitfit.injurylog.repository

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface InjuryRepository : CrudRepository<Injury, Long> {
    @Query("from Injury i where i.user = :user")
    fun findAll(user: User): Iterable<Injury>
}
