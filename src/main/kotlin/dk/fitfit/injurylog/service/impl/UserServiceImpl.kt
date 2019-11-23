package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.UserRepository
import dk.fitfit.injurylog.service.UserService
import javax.inject.Singleton

@Singleton
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun findAll(): Iterable<User> {
        return userRepository.findAll()
    }
}
