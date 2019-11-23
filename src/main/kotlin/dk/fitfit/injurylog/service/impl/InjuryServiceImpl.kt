package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.Injury
import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.InjuryRepository
import dk.fitfit.injurylog.service.InjuryService
import javax.inject.Singleton

@Singleton
class InjuryServiceImpl(private val injuryRepository: InjuryRepository) : InjuryService {
    override fun save(injury: Injury): Injury {
        return injuryRepository.save(injury)
    }

    override fun findAll(user: User): Iterable<Injury> {
        return injuryRepository.findAll(user)
    }
}
