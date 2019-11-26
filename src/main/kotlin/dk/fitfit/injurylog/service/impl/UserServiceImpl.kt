package dk.fitfit.injurylog.service.impl

import dk.fitfit.injurylog.domain.User
import dk.fitfit.injurylog.repository.UserRepository
import dk.fitfit.injurylog.service.UserService
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
open class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun save(user: User): User {
        val saved = userRepository.save(user)
        saved.roles.size
        return saved
    }

    override fun getByEmail(email: String): User = userRepository.findByEmail(email) ?: throw UserNotFoundException(email)

    override fun findAll(): Iterable<User> = userRepository.findAll()
}

class UserNotFoundException(email: String) : RuntimeException("No user found with the email: $email")
