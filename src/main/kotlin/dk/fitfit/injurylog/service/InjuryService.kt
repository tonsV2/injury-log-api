package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User

interface InjuryService {
    fun save(injury: Injury): Injury
    fun findAll(user: User): Iterable<Injury>
}
